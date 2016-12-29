package ooo.oxo.excited.database

/**
 * Created by seasonyuu on 2016/12/20.
 */
class QuerySQL {
    companion object {
        val createFavorite = """
                create table favorite(
                    user_id text,
                    token text,
                    id text,
                    title text,
                    cover text,
                    head_icon text,
                    head_name text,
                    author_name text,
                    description text,
                    source text,
                    link text,
                    remains integer,
                    sum integer,
                    timestamp integer,
                    type text,
                    uuid text,
                    refined integer,
                    distance text,
                    ratio text,
                    primary key(user_id,token,id)
                )
                """

        @JvmStatic fun selectFavorite(token: String, id: String): String {
            return """
                select * from favorite where token = "$token" and user_id = "$id"
                    order by timestamp desc
            """
        }

        @JvmStatic fun createFavorite(): String {
            return createFavorite
        }
    }
}