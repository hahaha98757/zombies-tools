@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package kr.hahaha98757.zombiestools

import kr.hahaha98757.zombiestools.enums.Difficulty
import kr.hahaha98757.zombiestools.enums.Difficulty.*
import kr.hahaha98757.zombiestools.enums.GameMode
import kr.hahaha98757.zombiestools.enums.Map
import kr.hahaha98757.zombiestools.enums.Map.*
import kr.hahaha98757.zombiestools.exceptions.ScoreboardNotFoundException
import kr.hahaha98757.zombiestools.exceptions.UnknownMapException
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.event.ClickEvent
import net.minecraft.event.HoverEvent
import net.minecraft.scoreboard.ScorePlayerTeam
import net.minecraft.util.BlockPos
import net.minecraft.util.ChatComponentText
import net.minecraft.util.EnumChatFormatting

object Tools {
    const val LINE = "§e-----------------------------------------------------"
    const val VERSION = "1.0.0"

    init {
        println("Zombies Tools $VERSION is loaded")
    }

    /** Adds [text] on chat that only you can see. */
    fun addChat(text: String) {
        Minecraft.getMinecraft()?.thePlayer?.addChatComponentMessage(ChatComponentText(text))
    }

    /** Adds [text] with [LINE] on chat that only you can see. */
    fun addChatWithLine(text: String) {
        Minecraft.getMinecraft()?.thePlayer?.addChatComponentMessage(ChatComponentText("$LINE\n$text\n$LINE"))
    }

    /**
     * Adds the text contained [url] on chat that only you can see.
     *
     * @param beforeText A text before URL.
     * @param urlText A text contained URL.
     * @param url A URL.
     * @param urlHoverText A text that will be displayed when a mouse hovers on the URL.
     * @param afterText A text after URL.
     */
    fun addChatWithURL(beforeText: String, urlText: String, url: String, urlHoverText: String, afterText: String) {
        val url1 = ChatComponentText(urlText)

        url1.chatStyle.chatClickEvent = ClickEvent(ClickEvent.Action.OPEN_URL, url)
        url1.chatStyle.chatHoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, ChatComponentText(urlHoverText))

        val text = ChatComponentText("")

        text.appendText(beforeText)
        text.appendSibling(url1)
        text.appendText(afterText)

        Minecraft.getMinecraft()?.thePlayer?.addChatComponentMessage(text)
    }

    /** Sends [text] on chat that everyone can see. */
    fun sendChat(text: String) {
        Minecraft.getMinecraft()?.thePlayer?.sendChatMessage(text)
    }

    /** Plays a sound with 1.0 volume */
    fun playSound(name: String, pitch: Float) {
        Minecraft.getMinecraft()?.thePlayer?.playSound(name, 1F, pitch)
    }

    /** Gets a width of the screen. */
    fun getX(): Int {
        return ScaledResolution(Minecraft.getMinecraft()).scaledWidth
    }

    /** Gets a width of [text] minus the width of the screen. */
    fun getX(text: String): Int {
        return getX() - Minecraft.getMinecraft().fontRendererObj.getStringWidth(text)
    }

    /** Gets a height of the screen. */
    fun getY(): Int {
        return ScaledResolution(Minecraft.getMinecraft()).scaledHeight
    }

    /** Gets a height of the text minus the height of the screen. */
    fun getYFont(): Int {
        return getY() - Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT
    }

    /**
     * Gets a scoreboard as HashMap.
     * The scores changed to keys.
     * The names(e.g. "Round 1", "Zombies Left: 3", and "player_name: REVIVE") changed to values.
     *
     * @throws ScoreboardNotFoundException If it can't find the scoreboard of Zombies.
     */
    fun getScoreboard(): HashMap<Int, String?> {
        val scoreboards = HashMap<Int, String?>()
        if (Minecraft.getMinecraft().theWorld == null || Minecraft.getMinecraft().thePlayer == null) throw ScoreboardNotFoundException("The world or the player is null")

        val scoreboard = Minecraft.getMinecraft().theWorld.scoreboard
        val sidebar = scoreboard.getObjectiveInDisplaySlot(1)

        if (sidebar == null || EnumChatFormatting.getTextWithoutFormattingCodes(sidebar.displayName) != "ZOMBIES") throw ScoreboardNotFoundException("You're not playing Zombies.")

        for (score in scoreboard.getSortedScores(sidebar)) for (i in 1..15) if (score.scorePoints == i)
            scoreboards[i] = EnumChatFormatting.getTextWithoutFormattingCodes(ScorePlayerTeam.formatPlayerName(scoreboard.getPlayersTeam(score.playerName), score.playerName)).replace(Regex("[^A-Za-z0-9가-힣:._,/\\s]"), "").trim()
        return scoreboards
    }

    /**
     * Checks if you're playing Hypixel.
     *
     * @throws ScoreboardNotFoundException If it can't find the scoreboard of Zombies.
     */
    fun isHypixel(): Boolean {
        val serverIP = getScoreboard()[1]
        return serverIP != null && serverIP == "www.hypixel.net"
    }

    /**
     * Checks to occur [ScoreboardNotFoundException]
     *
     * @return Returns false if [ScoreboardNotFoundException] is not occurred.
     */
    fun isNotZombies(): Boolean {
        try {
            getScoreboard()
            return false
        } catch (e: ScoreboardNotFoundException) {
            return true
        }
    }

    /** Checks if you're not in-game. */
    fun isNotPlayZombies(): Boolean {
        try {
            val scoreboard = getScoreboard()

            if (isHypixel()) {
                val str = scoreboard[10]
                return str == null || !str.contains(Minecraft.getMinecraft().thePlayer.name)
            }

            for (i in 7..10) {
                val str = scoreboard[i] ?: continue

                if (str.contains(Minecraft.getMinecraft().thePlayer.name)) return false
            }
            return true
        } catch (e: ScoreboardNotFoundException) {
            return true
        }

    }

    /**
     * Gets a status of each player as array.
     *
     * A number of the player with revive are stored at index 0.
     *
     * A number of the player with dead are stored at index 1.
     *
     * A number of the player with quit are stored at index 2.
     */
    fun getRevDeadQuit(): ByteArray {
        val rdq: ByteArray = byteArrayOf(0, 0, 0)

        val scoreboard = getScoreboard()

        for (i in 7..10) {
            var str = scoreboard[i] ?: continue

            try {
                str = str.split(":")[1].trim()
            } catch (e: IndexOutOfBoundsException) {
                continue
            }

            when (str) {
                "REVIVE", "부활" -> rdq[0]++
                "DEAD", "사망" -> rdq[1]++
                "QUIT", "떠남" -> rdq[2]++
            }
        }
        return rdq
    }

    /**
     * Gets [map].
     *
     * @throws UnknownMapException If it can't get [map].
     */
    fun getMap(): Map {
        val world = Minecraft.getMinecraft().theWorld ?: throw UnknownMapException("The world is null")

        val pos = BlockPos(44, 71, 0)

        if (world.isBlockLoaded(pos)) throw UnknownMapException("The world is not loaded")

        return when (world.getBlockState(pos).block.unlocalizedName) {
            "tile.air" -> DEAD_END
            "tile.cloth" -> BAD_BLOOD
            "tile.stoneSlab" -> ALIEN_ARCADIUM
            "tile.woodSlab" -> PRISON
            else -> throw UnknownMapException("Detected unknown map")
        }
    }

    /**
     * Gets [GameMode].
     *
     * Combine [getMap] and [difficulty]
     *
     * @throws UnknownMapException If it can't get [map].
     */
    fun getGameMode(difficulty: Difficulty): GameMode {
        val map = getMap()

        when (map) {
            DEAD_END ->
                return when (difficulty) {
                    NORMAL -> GameMode.DEAD_END_NORMAL
                    HARD -> GameMode.DEAD_END_HARD
                    RIP -> GameMode.DEAD_END_RIP
                }
            BAD_BLOOD ->
                return when (difficulty) {
                    NORMAL -> GameMode.BAD_BLOOD_NORMAL
                    HARD -> GameMode.BAD_BLOOD_HARD
                    RIP -> GameMode.BAD_BLOOD_RIP
                }
            ALIEN_ARCADIUM -> return GameMode.ALIEN_ARCADIUM
            PRISON ->
                return when (difficulty) {
                    NORMAL -> GameMode.PRISON_NORMAL
                    HARD -> GameMode.PRISON_HARD
                    RIP -> GameMode.PRISON_RIP
                }
        }
    }

    /**
     * Gets a round.
     *
     * @throws ScoreboardNotFoundException If it can't find the scoreboard of Zombies.
     */
    fun getRound(): Byte {
        val scoreboard = getScoreboard()

        if (isHypixel()) {
            val str = scoreboard[13]
            return str?.replace(Regex("[^0-9]"), "")?.toByte() ?: 0
        }

        for (i in 10..13) {
            val str = scoreboard[i] ?: continue
            if (!str.contains(":") && str.contains("Round")) return str.replace(Regex("[^0-9]"), "").toByte()
        }
        return 0
    }
}