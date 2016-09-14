import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.DataFrame
import org.apache.spark._
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD
import scala.util.hashing.MurmurHash3
import spark.implicits._

// Importing the data
val untypedDf = spark.read.format("com.databricks.spark.csv").option("header", "true").load("data/data.csv")
untypedDf.show()
untypedDf.printSchema()
untypedDf.select($"event").groupBy($"event").count().show()

case class Event(timestamp: String, event: String, cookie_id: String, email: String)
val df = untypedDf.as[Event]

// Exploring Events with sql
df.createOrReplaceTempView("events")
spark.sql("SELECT event, cookie_id FROM events WHERE email = 'a@a.com'").show()
spark.sql("SELECT event, count(*) FROM events GROUP BY event ORDER BY count(*) DESC").show()
spark.sql("SELECT * FROM events WHERE event = 'page_view'").collect().foreach(println)

// Finding the Connected Components
df.filter(e => e.cookie_id == "" || e.email == "").count() // How many events have both IDs?

def toId(str: String): (Long, String) = (MurmurHash3.stringHash(str), str)

val ids = df.
  filter(_.cookie_id != "").
  filter(_.email != "").
  flatMap(e => List(toId(e.cookie_id), toId(e.email))).
  distinct()

val edges = df.
  filter(_.cookie_id != "").
  filter(_.email != "").
  map(e => (MurmurHash3.stringHash(e.cookie_id), MurmurHash3.stringHash(e.email))).
  distinct()

val typedEdges = edges.map(x => Edge(x._1.toLong, x._2.toLong, 1))
val defaultVertice = ("Missing")
val graph = Graph(ids.rdd, typedEdges.rdd, defaultVertice)
graph.cache()
graph.numVertices
graph.numEdges

val cc = graph.connectedComponents() // 528s!

// Mapping Our IDs to the Components
val mappings = cc.vertices.toDF("vertex_id", "component")
val output = ids.toDF("v_id", "id").
  join(mappings, $"v_id" === $"vertex_id", "left").
  select("id", "component")

output.show()

output.repartition(1).write.format("com.databricks.spark.csv").save("data/output")
