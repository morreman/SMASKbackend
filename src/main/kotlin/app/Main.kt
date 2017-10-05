package app

import io.javalin.Javalin
import java.util.concurrent.atomic.AtomicInteger
import com.fasterxml.jackson.module.kotlin.*
import io.javalin.Context


data class Song(val  songName: String, val artist: String,
                var points: Int, var currentPoints: Int, var startNumber: Int)

val mapper = jacksonObjectMapper()


fun main(args: Array<String>)  {

    val userDao = UserDao()
    val app = Javalin.create().port(7000).start()

    with(app) {

        get("/") { ctx ->
            allowCrossOrigin(ctx)
            ctx.json("Hello Everybody")
        }

        get("/songs") { ctx ->
            allowCrossOrigin(ctx)
            ctx.json(userDao.songs)
            println("Försöker iaf")
        }

        get("/songs/:startNumber") { ctx ->
            allowCrossOrigin(ctx)
            ctx.json(userDao.findByStartNumber(ctx.param("startNumber")!!.toInt())!!)
        }

        get("/songs/artist/:artist") { ctx ->
            allowCrossOrigin(ctx)
            ctx.json(userDao.findByArtist(ctx.param("artist")!!)!!)
        }

        post("/songs/create") { ctx ->
            allowCrossOrigin(ctx)
            val song = mapper.readValue<Song>(ctx.body())
            println(song)

            userDao.save(songName = song.songName, artist = song.artist, points = song.points,
                    currentPoints = song.currentPoints)
            ctx.status(201)
        }

        put("/songs/update/:startNumber") { ctx ->
            allowCrossOrigin(ctx)
            val song = mapper.readValue<Song>(ctx.body())
            println("Försöker uppdatera $song")
            //val song = ctx.bodyAsClass(Song::class.java)
            userDao.update(
                    startNumber = ctx.param("startNumber")!!.toInt(),
                    song = song
            )
            ctx.status(204)
        }

        delete("/songs/delete/:startNumber") { ctx ->
            userDao.delete(ctx.param("startNumber")!!.toInt())
            ctx.status(204)
        }

        exception(Exception::class.java) { e, ctx ->
            e.printStackTrace()
        }

        error(404) { ctx ->
            ctx.json("error");
        }
    }
}

/**
 *
 */
fun allowCrossOrigin(ctx: Context) {
    ctx.response().setHeader("Access-Control-Allow-Origin", "*")
    ctx.response().setHeader("Access-Control-Allow-Methods", "GET, PUT, POST, DELETE, OPTIONS")
}

class UserDao {

    val songs = hashMapOf(
            0 to Song(songName = "Forever Young", artist = "Morre and the boys",
                    points = 0, currentPoints = 0, startNumber = 0),
            1 to Song(songName = "Never old", artist = "The capitals",
                    points = 0, currentPoints = 0, startNumber = 1),
            2 to Song(songName = "Always everytime", artist = "Rihanna",
                    points = 0, currentPoints = 0, startNumber = 2),
            3 to Song(songName = "Me and the you", artist = "Rammstein",
                    points = 0, currentPoints = 0, startNumber = 3)
    )

    var lastId: AtomicInteger = AtomicInteger(songs.size -1)

    fun save(songName: String, artist: String, points: Int, currentPoints: Int) {
        val startNumber = lastId.incrementAndGet()
        songs.put(startNumber, Song(songName = songName, artist = artist, startNumber = startNumber,
                currentPoints = currentPoints, points = points))
        println(songs)
    }

    fun findByStartNumber(startNumber: Int): Song? {
        return songs[startNumber]
    }

    fun findByArtist(artist: String): Song? {
        return songs.values.find { it.artist == artist }
    }

    fun update(startNumber: Int, song: Song) {
        for (x in songs.keys){
            if (x.equals(startNumber)){
                songs[x] = Song(startNumber = startNumber, artist = song.artist, songName = song.songName,
                        points = song.points, currentPoints = song.currentPoints)
            }
        }
        //songs.put(startNumber, Song(songName = song.songName, artist = song.artist,
               // points = song.points, currentPoints = song.currentPoints, startNumber = startNumber))
    }

    fun delete(startNumber: Int) {
        songs.remove(startNumber)
    }
}