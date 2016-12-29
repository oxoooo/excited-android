package ooo.oxo.excited.api

import android.text.TextUtils
import java.net.URLEncoder

class QueryField {
    companion object {

        val cardItem = """
                        id
                        title
                        cover
                        head_icon
                        head_name
                        author_name
                        description(num:140)
                        source
                        link
                        remains
                        sum
                        timestamp
                        type
                        uuid
                        refined
                        distance
                        ratio
        """

        val cards = """
                {
                    cards {
                       $cardItem
                    }
                }
            """

        val card = """
                    card {
                        $cardItem
                    }
            """

        /**
         * @param 是否需要获得关注状态
         */
        @JvmStatic fun queryChannel(followed: Boolean): String {
            val str: String
            if (followed)
                str = "followed"
            else
                str = ""
            return """
             {
                 channels
                 {
                     id
                     icon
                     name
                     description
                     createdAt
                     $str
                 }
             }
             """
        }

        /**
         * @param channelId 要进行关注/取消关注的channelId
         * @param followed true则关注，false则取消关注
         */
        @JvmStatic fun setFollowState(channelId: String, followed: Boolean): String {
            return """
                mutation{
                    setFollowState(input:{channelId:$channelId,state:$followed}){
                        channel{
                            followed
                        }
                    }
                }
            """
        }

        @JvmStatic fun timeline(): String {
            return """
                {
                    timeline {
                       $cardItem
                    }
                }
            """
        }

        @JvmStatic fun cards(type: String?, desc: Boolean, limit: Int, channelId: String): String {
            return """
                {
                    cards (${if (type == null) "" else "type:$type,"}desc:$desc,
                    ${if (limit == 0) "" else "limit:$limit,"}channelId:$channelId) {
                        $cardItem
                    }
                }
            """
        }

        @JvmStatic fun previewCard(url: String): String {
            return """
                mutation{
                    previewCard(input:{url:"$url"}){
                        card{
                            title
                            description
                            cover
                            head_icon
                            head_name
                            author_name
                            type
                        }
                    }
                }
            """
        }

        @JvmStatic fun addImageCard(url: String,
                                    description: String, channelId: String): String {
            return """
            mutation{
                addImageCard(input:{url:"$url", description:"$description",
                channelId:$channelId}){
                    card{
                        $cardItem
                    }
                    notice{
                        text
                        type
                    }
                }
            }
            """
        }

        @JvmStatic fun addWebCard(url: String, title: String?,
                                  description: String?, channelId: String): String {
            return """
            mutation{
                addWebCard(input:{url:"$url",
                ${if (TextUtils.isEmpty(title)) "" else "title:\"$title\","}
                ${if (TextUtils.isEmpty(description)) "" else "description:\"$description\","}
                channelId:$channelId}){
                    card{
                        $cardItem
                    }
                    notice{
                        text
                        type
                    }
                }
            }
            """
        }

        @JvmStatic fun favorites(): String {
            return """
                {
                    user {
                        cards {
                            $cardItem
                        }
                    }
                }
            """
        }

        @JvmStatic fun vote(cardId: String): String {
            return """
                mutation {
                    voteCard(input : {cardId: $cardId}) {
                        $card
                    }
                }
            """
        }

        @JvmStatic fun checkLogin(): String {
            return """
                {
                    user {
                        id
                        token
                    }
                }
            """
        }
    }

}