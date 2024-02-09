package com.wafflestudio.ai.icebreaker

import com.github.instagram4j.instagram4j.IGClient
import com.github.instagram4j.instagram4j.models.media.timeline.*
import com.github.instagram4j.instagram4j.requests.feed.FeedUserRequest
import com.github.instagram4j.instagram4j.requests.users.UsersUsernameInfoRequest
import org.junit.jupiter.api.Test

/**
 * Instagram4j 테스트
 *
 * https://github.com/instagram4j/instagram4j
 */
class InstagramClientTest {

    @Test
    fun `계정 조회`() {
        val req = UsersUsernameInfoRequest("hanwhaeagles_soori")
        val resp = req.execute(igClient).join()

        println(resp.user)
    }

    @Test
    fun `피드 이미지 조회`() {
        val req = UsersUsernameInfoRequest("hanwhaeagles_soori")
        val resp = req.execute(igClient).join()

        val feeds = FeedUserRequest(resp.user.pk)
            .execute(igClient).join()

        println("feed image or video urls")

        feeds.items.forEach { item ->
            when (item.media_type) {
                "1" -> { // image
                    item as TimelineImageMedia
                    println(item.image_versions2.candidates.firstOrNull()?.url)
                }

                "2" -> { // video
                    item as TimelineVideoMedia
                    println(item.video_versions.firstOrNull()?.url)
                }

                "8" -> { // carousel
                    item as TimelineCarouselMedia
                    item.carousel_media.forEach {
                        when (it.media_type) {
                            "1" -> {
                                it as ImageCarouselItem
                                println(it.image_versions2.candidates.firstOrNull()?.url)
                            }

                            "2" -> {
                                it as VideoCarouselItem
                                println(it.video_versions.firstOrNull()?.url)
                            }
                        }
                    }
                }
            }
            println("====================")
        }
    }

    @Test
    fun `피드 해쉬태그 조회`() {
        val req = UsersUsernameInfoRequest("hanwhaeagles_soori")
        val resp = req.execute(igClient).join()

        val feeds = FeedUserRequest(resp.user.pk)
            .execute(igClient).join()

        println("feed hashtags")
        feeds.items.forEach { item ->
            val text = item.caption.text
            val regex = Regex("#[\\w가-힣]+")
            val hashTags = regex.findAll(text).map { it.value }.toList()
            println(hashTags)
            println("====================")
        }
    }

    companion object {
        val igClient: IGClient = IGClient.builder()
            .username("YOUR_ID")
            .password("YOUR_PW")
            .login()
    }

}