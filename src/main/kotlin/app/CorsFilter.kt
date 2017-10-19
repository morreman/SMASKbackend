package app

import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.util.concurrent.atomic.AtomicInteger

@Component
open class CorsFilter : WebFilter {

    override fun filter(ctx: ServerWebExchange?, chain: WebFilterChain?): Mono<Void> {
        if (ctx != null) {
            ctx.response.headers.add("Access-Control-Allow-Origin", "*")
            ctx.response.headers.add("Access-Control-Allow-Methods", "GET, PUT, POST, DELETE, OPTIONS")
            ctx.response.headers.add("Access-Control-Allow-Headers", "DNT,X-CustomHeader,Keep-Alive,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,Content-Range,Range")

            if (ctx.request.method == HttpMethod.OPTIONS) {
                ctx.response.headers.add("Access-Control-Max-Age", "1728000")
                ctx.response.statusCode = HttpStatus.NO_CONTENT
                return Mono.empty()
            } else {
                ctx.response.headers.add("Access-Control-Expose-Headers", "DNT,X-CustomHeader,Keep-Alive,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,Content-Range,Range")
                return chain?.filter(ctx) ?: Mono.empty()
            }
        } else {
            return chain?.filter(ctx) ?: Mono.empty()
        }
    }
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
        //(songs[x].startNumber.equals(startNumber))
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