package app

import io.javalin.Javalin
import com.fasterxml.jackson.module.kotlin.*
import io.javalin.Context

data class Song(val  songName: String, val artist: String,
                var points: Int, var currentPoints: Int, var startNumber: Int)

val mapper = jacksonObjectMapper()


fun main(args: Array<String>)  {
    val storage = Storage()
    storage.setup()
    val app = Javalin.create().port(7000).start()

    with(app) {

        get("/") { ctx ->
            allowCrossOrigin(ctx)
            ctx.json(storage.getSongs())
        }

        get("/songs") { ctx ->
            allowCrossOrigin(ctx)
            ctx.json(storage.getSongs())
        }

        get("/songs/:startNumber") { ctx ->
            allowCrossOrigin(ctx)
            ctx.json(storage.getSong(ctx.param("startnumber")!!.toInt()))
        }

        get("/songs/artist/:artist") { ctx ->
            allowCrossOrigin(ctx)
            ctx.json(storage.getSongByArtist(ctx.param("artist")!!.toString().toLowerCase()))
        }

        post("/songs/create") { ctx ->
            allowCrossOrigin(ctx)
            storage.addSongObject(mapper.readValue<Song>(ctx.body()))
            printDB()
            ctx.status(201)
        }


        put("/songs/update/:startNumber") { ctx ->
            allowCrossOrigin(ctx)
            storage.updateSongObject(mapper.readValue<Song>(ctx.body()))
            printDB()
            ctx.status(204)
        }

        delete("/songs/delete/:startNumber") { ctx ->
            allowCrossOrigin(ctx)
            storage.deleteSongObject(ctx.param("startnumber")!!.toInt())
            printDB()
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
 * Allowing cross origin.
 */
fun allowCrossOrigin(ctx: Context) {
    ctx.response().setHeader("Access-Control-Allow-Origin", "*")
    ctx.response().setHeader("Access-Control-Allow-Methods", "GET, PUT, POST, DELETE, OPTIONS")
    ctx.response().setHeader("Access-Control-Max-Age", "1000")
    ctx.response().setHeader("Access-Control-Allow-Headers", "DNT,X-CustomHeader,Keep-Alive,User-Agent," +
            "X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,Content-Range,Range")
}

/**
 * Method that prints DB.
 */
fun printDB() {
    val storage = Storage()
    println(storage.getSongs())
}