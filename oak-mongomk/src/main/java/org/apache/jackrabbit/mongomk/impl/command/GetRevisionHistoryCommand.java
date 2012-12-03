begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mongomk
operator|.
name|impl
operator|.
name|command
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mk
operator|.
name|api
operator|.
name|MicroKernelException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mk
operator|.
name|json
operator|.
name|JsopBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mk
operator|.
name|model
operator|.
name|tree
operator|.
name|DiffBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mongomk
operator|.
name|api
operator|.
name|model
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mongomk
operator|.
name|impl
operator|.
name|MongoNodeStore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mongomk
operator|.
name|impl
operator|.
name|action
operator|.
name|FetchCommitsAction
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mongomk
operator|.
name|impl
operator|.
name|model
operator|.
name|MongoCommit
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mongomk
operator|.
name|impl
operator|.
name|model
operator|.
name|tree
operator|.
name|SimpleMongoNodeStore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mongomk
operator|.
name|util
operator|.
name|MongoUtil
import|;
end_import

begin_comment
comment|/**  * A {@code Command} for {@code MongoMicroKernel#getRevisionHistory(long, int, String)}  */
end_comment

begin_class
specifier|public
class|class
name|GetRevisionHistoryCommand
extends|extends
name|BaseCommand
argument_list|<
name|String
argument_list|>
block|{
specifier|private
specifier|final
name|long
name|since
decl_stmt|;
specifier|private
name|int
name|maxEntries
decl_stmt|;
specifier|private
name|String
name|path
decl_stmt|;
comment|/**      * Constructs a {@code GetRevisionHistoryCommandMongo}      *      * @param nodeStore Node store.      * @param since Timestamp (ms) of earliest revision to be returned      * @param maxEntries maximum #entries to be returned; if< 0, no limit will be applied.      * @param path optional path filter; if {@code null} or {@code ""} the      * default ({@code "/"}) will be assumed, i.e. no filter will be applied      */
specifier|public
name|GetRevisionHistoryCommand
parameter_list|(
name|MongoNodeStore
name|nodeStore
parameter_list|,
name|long
name|since
parameter_list|,
name|int
name|maxEntries
parameter_list|,
name|String
name|path
parameter_list|)
block|{
name|super
argument_list|(
name|nodeStore
argument_list|)
expr_stmt|;
name|this
operator|.
name|since
operator|=
name|since
expr_stmt|;
name|this
operator|.
name|maxEntries
operator|=
name|maxEntries
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|execute
parameter_list|()
block|{
name|path
operator|=
name|MongoUtil
operator|.
name|adjustPath
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|maxEntries
operator|=
name|maxEntries
operator|<
literal|0
condition|?
name|Integer
operator|.
name|MAX_VALUE
else|:
name|maxEntries
expr_stmt|;
name|FetchCommitsAction
name|action
init|=
operator|new
name|FetchCommitsAction
argument_list|(
name|nodeStore
argument_list|)
decl_stmt|;
name|action
operator|.
name|setMaxEntries
argument_list|(
name|maxEntries
argument_list|)
expr_stmt|;
name|action
operator|.
name|includeBranchCommits
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|MongoCommit
argument_list|>
name|commits
init|=
name|action
operator|.
name|execute
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|MongoCommit
argument_list|>
name|history
init|=
operator|new
name|ArrayList
argument_list|<
name|MongoCommit
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|commits
operator|.
name|size
argument_list|()
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|MongoCommit
name|commit
init|=
name|commits
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|commit
operator|.
name|getTimestamp
argument_list|()
operator|>=
name|since
condition|)
block|{
if|if
condition|(
name|MongoUtil
operator|.
name|isFiltered
argument_list|(
name|path
argument_list|)
condition|)
block|{
try|try
block|{
name|String
name|diff
init|=
operator|new
name|DiffBuilder
argument_list|(
name|MongoUtil
operator|.
name|wrap
argument_list|(
name|getNode
argument_list|(
literal|"/"
argument_list|,
name|commit
operator|.
name|getBaseRevisionId
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|MongoUtil
operator|.
name|wrap
argument_list|(
name|getNode
argument_list|(
literal|"/"
argument_list|,
name|commit
operator|.
name|getRevisionId
argument_list|()
argument_list|)
argument_list|)
argument_list|,
literal|"/"
argument_list|,
operator|-
literal|1
argument_list|,
operator|new
name|SimpleMongoNodeStore
argument_list|()
argument_list|,
name|path
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|diff
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|history
operator|.
name|add
argument_list|(
name|commit
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MicroKernelException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|history
operator|.
name|add
argument_list|(
name|commit
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|JsopBuilder
name|buff
init|=
operator|new
name|JsopBuilder
argument_list|()
operator|.
name|array
argument_list|()
decl_stmt|;
for|for
control|(
name|MongoCommit
name|commit
range|:
name|history
control|)
block|{
name|buff
operator|.
name|object
argument_list|()
operator|.
name|key
argument_list|(
literal|"id"
argument_list|)
operator|.
name|value
argument_list|(
name|MongoUtil
operator|.
name|fromMongoRepresentation
argument_list|(
name|commit
operator|.
name|getRevisionId
argument_list|()
argument_list|)
argument_list|)
operator|.
name|key
argument_list|(
literal|"ts"
argument_list|)
operator|.
name|value
argument_list|(
name|commit
operator|.
name|getTimestamp
argument_list|()
argument_list|)
operator|.
name|key
argument_list|(
literal|"msg"
argument_list|)
operator|.
name|value
argument_list|(
name|commit
operator|.
name|getMessage
argument_list|()
argument_list|)
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
return|return
name|buff
operator|.
name|endArray
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
name|Node
name|getNode
parameter_list|(
name|String
name|path
parameter_list|,
name|long
name|revisionId
parameter_list|)
throws|throws
name|Exception
block|{
return|return
operator|new
name|GetNodesCommandNew
argument_list|(
name|nodeStore
argument_list|,
name|path
argument_list|,
name|revisionId
argument_list|)
operator|.
name|execute
argument_list|()
return|;
block|}
block|}
end_class

end_unit

