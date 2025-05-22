rootProject.name = "board"

include(":common")
include(":common:snowflake")
include(":common:data-serializer")
include(":common:event")
include(":common:outbox-message-relay")
include(":service")
include(":service:article")
include(":service:article-read")
include(":service:hot-article")
include(":service:comment")
include(":service:view")
include(":service:like")