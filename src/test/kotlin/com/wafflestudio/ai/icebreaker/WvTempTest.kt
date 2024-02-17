package com.wafflestudio.ai.icebreaker


import com.wafflestudio.ai.icebreaker.application.common.WeaviatePort
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.net.URI

/**
 * Weaviate 작동 여부 테스트.
 */
@SpringBootTest
class WvTempTest @Autowired constructor (
    val weaviatePort: WeaviatePort
) {

    @Test
    fun `스키마 추가`() {
        weaviatePort.addSchema("Taro")
    }

    fun `스키마 조회`() {
        weaviatePort.getSchema()
    }

    @Test
    fun `데이터 조회`() {
        weaviatePort.getAll("Taro")
    }

    @Test
    fun `데이터 저장`() {
//        val json = URI("https://raw.githubusercontent.com/weaviate-tutorials/quickstart/main/data/jeopardy_tiny.json").toURL().readText()
//        weaviatePort.save("Question", jacksonObjectMapper().readValue<List<Question>>(json))
        val list = listOf(
            "This card with a chalice may represent the Ace of Cups, symbolizing new beginnings in emotions, relationships, or creative endeavors. The moon phases indicate changes and cycles.",
            "This card featuring the sun and moons could represent balance between different forces in life, reminiscent of the Temperance card, suggesting harmony and moderation.",
            "The hand holding flowers next to a book might symbolize learning or discovering beauty and knowledge, possibly relating to the Page or Ace of Swords, which often indicate intellectual insight and new ideas.",
            "A card with a sun suggests vitality, success, and joy, often associated with The Sun card, bringing positivity and clarity to a situation.",
            "An hourglass with the moon phases can indicate the passing of time and the need for patience, possibly reflecting the Seven of Pentacles, which is about waiting for rewards from hard work.",
            "This card with crystals might be related to the material and physical aspects of life, like the Pentacles suit, suggesting a focus on tangible outcomes or materialization of goals.",
            "The moon with hands could symbolize intuition and the subconscious, similar to The Moon card, which is about the unseen, dreams, and intuition.",
            "Mushrooms growing in the moonlight suggest growth in darkness, could be akin to the Death card which is not necessarily about physical death but transformation and the need to let go for new growth.\n",
            "A serpent and moon phases might represent rebirth or regeneration, akin to the symbolism in The Tower or Death cards, where upheaval leads to transformation.\n",
            "The radiant sun with a calm face may symbolize The Sun tarot card, representing success, radiance, and abundance. The sun's rays suggest vitality and the triangles could imply a connection to higher wisdom or spirituality.\n",
            "The crescent moons cradling a full moon could represent the waxing and waning phases of life, akin to The Moon card, which symbolizes the unconscious, intuition, and the unveiling of illusions.\n",
            "The hourglass, containing sun and moon, suggests the passage of time and the endless cycle of day and night. It may symbolize the Two of Pentacles, representing the need for balance in change and resourcefulness in adapting.\n",
            "The moth circling around a moon may represent The Fool's journey towards the unknown, signifying faith and the willingness to follow one's instincts or the call of the soul, much like The Fool card.\n",
            "The serpent, a symbol of transformation and healing, coiled around a lemniscate (infinity symbol), could be akin to The Magician, representing infinite potential and the ouroboros of continuous cycles of renewal.\n",
            "The mushroom and phases of the moon can represent growth and evolution. This might be tied to The Empress card, symbolizing fertility, creativity, and the nurturing of ideas until they come to fruition.\n",
            "The dual sun and moon suggest a balance between the conscious and unconscious, similar to The Temperance card, representing harmony, moderation, and the merging of opposites.\n",
            "The mushroom within a geometric pattern suggests a grounded yet magical growth, potentially resembling The Hierophant, which stands for tradition, learning from the past, and the grounding effect of nature.\n",
            "The hand with potion and mushrooms symbolizes the alchemical process of transformation, akin to The Magician, who takes the raw materials of reality and transforms them with skill and will.\n",
            "The hand with crystals and a bird could represent the Page of Swords, symbolizing intellectual curiosity, the pursuit of truth, and the freedom of thought, as birds are often associated with the element of air and the realm of the mind.\n",
            "The sun with multiple faces may reflect the multifaceted nature of self or consciousness, similar to The Sun card, which speaks to joy and the synthesis of various aspects of one's life to find harmony and enlightenment.",
        )
        var id = 1
        for (row in list) {
            weaviatePort.save("Taro", id, row)
            id++
        }
    }

    @Test
    fun `유사 텍스트 조회`() {
        weaviatePort.nearTextQuery("Taro", listOf("프랑스 여행"))
    }

}
