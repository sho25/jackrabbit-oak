begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mk
operator|.
name|server
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|MicroKernel
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
name|util
operator|.
name|MicroKernelInputStream
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
name|util
operator|.
name|IOUtils
import|;
end_import

begin_comment
comment|/**  * Servlet handling requests directed at a {@code MicroKernel} instance.  */
end_comment

begin_class
class|class
name|MicroKernelServlet
block|{
comment|/** The one and only instance of this servlet. */
specifier|public
specifier|static
name|MicroKernelServlet
name|INSTANCE
init|=
operator|new
name|MicroKernelServlet
argument_list|()
decl_stmt|;
comment|/** Just one instance, no need to make constructor public */
specifier|private
name|MicroKernelServlet
parameter_list|()
block|{}
specifier|public
name|void
name|service
parameter_list|(
name|MicroKernel
name|mk
parameter_list|,
name|Request
name|request
parameter_list|,
name|Response
name|response
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|file
init|=
name|request
operator|.
name|getFile
argument_list|()
decl_stmt|;
name|int
name|dotIndex
init|=
name|file
operator|.
name|indexOf
argument_list|(
literal|'.'
argument_list|)
decl_stmt|;
if|if
condition|(
name|dotIndex
operator|==
operator|-
literal|1
condition|)
block|{
name|dotIndex
operator|=
name|file
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
name|Command
name|command
init|=
name|COMMANDS
operator|.
name|get
argument_list|(
name|file
operator|.
name|substring
argument_list|(
literal|1
argument_list|,
name|dotIndex
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|command
operator|!=
literal|null
operator|&&
name|mk
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|command
operator|.
name|execute
argument_list|(
name|mk
argument_list|,
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MicroKernelException
name|e
parameter_list|)
block|{
name|response
operator|.
name|setStatusCode
argument_list|(
literal|500
argument_list|)
expr_stmt|;
name|response
operator|.
name|setContentType
argument_list|(
literal|"text/plain"
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|(
operator|new
name|PrintStream
argument_list|(
name|response
operator|.
name|getOutputStream
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|response
operator|.
name|setStatusCode
argument_list|(
literal|500
argument_list|)
expr_stmt|;
name|response
operator|.
name|setContentType
argument_list|(
literal|"text/plain"
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|(
operator|new
name|PrintStream
argument_list|(
name|response
operator|.
name|getOutputStream
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
name|response
operator|.
name|setStatusCode
argument_list|(
literal|404
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
interface|interface
name|Command
block|{
name|void
name|execute
parameter_list|(
name|MicroKernel
name|mk
parameter_list|,
name|Request
name|request
parameter_list|,
name|Response
name|response
parameter_list|)
throws|throws
name|IOException
throws|,
name|MicroKernelException
function_decl|;
block|}
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Command
argument_list|>
name|COMMANDS
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Command
argument_list|>
argument_list|()
decl_stmt|;
static|static
block|{
name|COMMANDS
operator|.
name|put
argument_list|(
literal|"getHeadRevision"
argument_list|,
operator|new
name|GetHeadRevision
argument_list|()
argument_list|)
expr_stmt|;
name|COMMANDS
operator|.
name|put
argument_list|(
literal|"getRevisionHistory"
argument_list|,
operator|new
name|GetRevisionHistory
argument_list|()
argument_list|)
expr_stmt|;
name|COMMANDS
operator|.
name|put
argument_list|(
literal|"waitForCommit"
argument_list|,
operator|new
name|WaitForCommit
argument_list|()
argument_list|)
expr_stmt|;
name|COMMANDS
operator|.
name|put
argument_list|(
literal|"getJournal"
argument_list|,
operator|new
name|GetJournal
argument_list|()
argument_list|)
expr_stmt|;
name|COMMANDS
operator|.
name|put
argument_list|(
literal|"diff"
argument_list|,
operator|new
name|Diff
argument_list|()
argument_list|)
expr_stmt|;
name|COMMANDS
operator|.
name|put
argument_list|(
literal|"nodeExists"
argument_list|,
operator|new
name|NodeExists
argument_list|()
argument_list|)
expr_stmt|;
name|COMMANDS
operator|.
name|put
argument_list|(
literal|"getChildNodeCount"
argument_list|,
operator|new
name|GetChildNodeCount
argument_list|()
argument_list|)
expr_stmt|;
name|COMMANDS
operator|.
name|put
argument_list|(
literal|"getNodes"
argument_list|,
operator|new
name|GetNodes
argument_list|()
argument_list|)
expr_stmt|;
name|COMMANDS
operator|.
name|put
argument_list|(
literal|"commit"
argument_list|,
operator|new
name|Commit
argument_list|()
argument_list|)
expr_stmt|;
name|COMMANDS
operator|.
name|put
argument_list|(
literal|"branch"
argument_list|,
operator|new
name|Branch
argument_list|()
argument_list|)
expr_stmt|;
name|COMMANDS
operator|.
name|put
argument_list|(
literal|"merge"
argument_list|,
operator|new
name|Merge
argument_list|()
argument_list|)
expr_stmt|;
name|COMMANDS
operator|.
name|put
argument_list|(
literal|"getLength"
argument_list|,
operator|new
name|GetLength
argument_list|()
argument_list|)
expr_stmt|;
name|COMMANDS
operator|.
name|put
argument_list|(
literal|"read"
argument_list|,
operator|new
name|Read
argument_list|()
argument_list|)
expr_stmt|;
name|COMMANDS
operator|.
name|put
argument_list|(
literal|"write"
argument_list|,
operator|new
name|Write
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|static
class|class
name|GetHeadRevision
implements|implements
name|Command
block|{
annotation|@
name|Override
specifier|public
name|void
name|execute
parameter_list|(
name|MicroKernel
name|mk
parameter_list|,
name|Request
name|request
parameter_list|,
name|Response
name|response
parameter_list|)
throws|throws
name|IOException
throws|,
name|MicroKernelException
block|{
name|response
operator|.
name|setContentType
argument_list|(
literal|"text/plain"
argument_list|)
expr_stmt|;
name|response
operator|.
name|write
argument_list|(
name|mk
operator|.
name|getHeadRevision
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|static
class|class
name|GetRevisionHistory
implements|implements
name|Command
block|{
annotation|@
name|Override
specifier|public
name|void
name|execute
parameter_list|(
name|MicroKernel
name|mk
parameter_list|,
name|Request
name|request
parameter_list|,
name|Response
name|response
parameter_list|)
throws|throws
name|IOException
throws|,
name|MicroKernelException
block|{
name|long
name|since
init|=
name|request
operator|.
name|getParameter
argument_list|(
literal|"since"
argument_list|,
literal|0L
argument_list|)
decl_stmt|;
name|int
name|maxEntries
init|=
name|request
operator|.
name|getParameter
argument_list|(
literal|"max_entries"
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|String
name|path
init|=
name|request
operator|.
name|getParameter
argument_list|(
literal|"path"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|response
operator|.
name|setContentType
argument_list|(
literal|"application/json"
argument_list|)
expr_stmt|;
name|String
name|json
init|=
name|mk
operator|.
name|getRevisionHistory
argument_list|(
name|since
argument_list|,
name|maxEntries
argument_list|,
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|request
operator|.
name|getHeaders
argument_list|()
operator|.
name|containsKey
argument_list|(
literal|"User-Agent"
argument_list|)
condition|)
block|{
name|json
operator|=
name|JsopBuilder
operator|.
name|prettyPrint
argument_list|(
name|json
argument_list|)
expr_stmt|;
block|}
name|response
operator|.
name|write
argument_list|(
name|json
argument_list|)
expr_stmt|;
block|}
block|}
specifier|static
class|class
name|WaitForCommit
implements|implements
name|Command
block|{
annotation|@
name|Override
specifier|public
name|void
name|execute
parameter_list|(
name|MicroKernel
name|mk
parameter_list|,
name|Request
name|request
parameter_list|,
name|Response
name|response
parameter_list|)
throws|throws
name|IOException
throws|,
name|MicroKernelException
block|{
name|String
name|headRevision
init|=
name|mk
operator|.
name|getHeadRevision
argument_list|()
decl_stmt|;
name|String
name|oldHead
init|=
name|request
operator|.
name|getParameter
argument_list|(
literal|"revision_id"
argument_list|,
name|headRevision
argument_list|)
decl_stmt|;
name|long
name|maxWaitMillis
init|=
name|request
operator|.
name|getParameter
argument_list|(
literal|"max_wait_millis"
argument_list|,
literal|0L
argument_list|)
decl_stmt|;
name|String
name|currentHead
decl_stmt|;
try|try
block|{
name|currentHead
operator|=
name|mk
operator|.
name|waitForCommit
argument_list|(
name|oldHead
argument_list|,
name|maxWaitMillis
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
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
name|response
operator|.
name|setContentType
argument_list|(
literal|"text/plain"
argument_list|)
expr_stmt|;
name|response
operator|.
name|write
argument_list|(
name|currentHead
operator|==
literal|null
condition|?
literal|"null"
else|:
name|currentHead
argument_list|)
expr_stmt|;
block|}
block|}
specifier|static
class|class
name|GetJournal
implements|implements
name|Command
block|{
annotation|@
name|Override
specifier|public
name|void
name|execute
parameter_list|(
name|MicroKernel
name|mk
parameter_list|,
name|Request
name|request
parameter_list|,
name|Response
name|response
parameter_list|)
throws|throws
name|IOException
throws|,
name|MicroKernelException
block|{
name|String
name|headRevision
init|=
name|mk
operator|.
name|getHeadRevision
argument_list|()
decl_stmt|;
name|String
name|fromRevisionId
init|=
name|request
operator|.
name|getParameter
argument_list|(
literal|"from_revision_id"
argument_list|,
name|headRevision
argument_list|)
decl_stmt|;
name|String
name|toRevisionId
init|=
name|request
operator|.
name|getParameter
argument_list|(
literal|"to_revision_id"
argument_list|,
name|headRevision
argument_list|)
decl_stmt|;
name|String
name|path
init|=
name|request
operator|.
name|getParameter
argument_list|(
literal|"path"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|response
operator|.
name|setContentType
argument_list|(
literal|"application/json"
argument_list|)
expr_stmt|;
name|String
name|json
init|=
name|mk
operator|.
name|getJournal
argument_list|(
name|fromRevisionId
argument_list|,
name|toRevisionId
argument_list|,
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|request
operator|.
name|getHeaders
argument_list|()
operator|.
name|containsKey
argument_list|(
literal|"User-Agent"
argument_list|)
condition|)
block|{
name|json
operator|=
name|JsopBuilder
operator|.
name|prettyPrint
argument_list|(
name|json
argument_list|)
expr_stmt|;
block|}
name|response
operator|.
name|write
argument_list|(
name|json
argument_list|)
expr_stmt|;
block|}
block|}
specifier|static
class|class
name|Diff
implements|implements
name|Command
block|{
annotation|@
name|Override
specifier|public
name|void
name|execute
parameter_list|(
name|MicroKernel
name|mk
parameter_list|,
name|Request
name|request
parameter_list|,
name|Response
name|response
parameter_list|)
throws|throws
name|IOException
throws|,
name|MicroKernelException
block|{
name|String
name|headRevision
init|=
name|mk
operator|.
name|getHeadRevision
argument_list|()
decl_stmt|;
name|String
name|fromRevisionId
init|=
name|request
operator|.
name|getParameter
argument_list|(
literal|"from_revision_id"
argument_list|,
name|headRevision
argument_list|)
decl_stmt|;
name|String
name|toRevisionId
init|=
name|request
operator|.
name|getParameter
argument_list|(
literal|"to_revision_id"
argument_list|,
name|headRevision
argument_list|)
decl_stmt|;
name|String
name|path
init|=
name|request
operator|.
name|getParameter
argument_list|(
literal|"path"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|int
name|depth
init|=
name|request
operator|.
name|getParameter
argument_list|(
literal|"depth"
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|response
operator|.
name|setContentType
argument_list|(
literal|"application/json"
argument_list|)
expr_stmt|;
name|String
name|json
init|=
name|mk
operator|.
name|diff
argument_list|(
name|fromRevisionId
argument_list|,
name|toRevisionId
argument_list|,
name|path
argument_list|,
name|depth
argument_list|)
decl_stmt|;
if|if
condition|(
name|request
operator|.
name|getHeaders
argument_list|()
operator|.
name|containsKey
argument_list|(
literal|"User-Agent"
argument_list|)
condition|)
block|{
name|json
operator|=
name|JsopBuilder
operator|.
name|prettyPrint
argument_list|(
name|json
argument_list|)
expr_stmt|;
block|}
name|response
operator|.
name|write
argument_list|(
name|json
argument_list|)
expr_stmt|;
block|}
block|}
specifier|static
class|class
name|NodeExists
implements|implements
name|Command
block|{
annotation|@
name|Override
specifier|public
name|void
name|execute
parameter_list|(
name|MicroKernel
name|mk
parameter_list|,
name|Request
name|request
parameter_list|,
name|Response
name|response
parameter_list|)
throws|throws
name|IOException
throws|,
name|MicroKernelException
block|{
name|String
name|headRevision
init|=
name|mk
operator|.
name|getHeadRevision
argument_list|()
decl_stmt|;
name|String
name|path
init|=
name|request
operator|.
name|getParameter
argument_list|(
literal|"path"
argument_list|,
literal|"/"
argument_list|)
decl_stmt|;
name|String
name|revisionId
init|=
name|request
operator|.
name|getParameter
argument_list|(
literal|"revision_id"
argument_list|,
name|headRevision
argument_list|)
decl_stmt|;
name|response
operator|.
name|setContentType
argument_list|(
literal|"text/plain"
argument_list|)
expr_stmt|;
name|response
operator|.
name|write
argument_list|(
name|Boolean
operator|.
name|toString
argument_list|(
name|mk
operator|.
name|nodeExists
argument_list|(
name|path
argument_list|,
name|revisionId
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|static
class|class
name|GetChildNodeCount
implements|implements
name|Command
block|{
annotation|@
name|Override
specifier|public
name|void
name|execute
parameter_list|(
name|MicroKernel
name|mk
parameter_list|,
name|Request
name|request
parameter_list|,
name|Response
name|response
parameter_list|)
throws|throws
name|IOException
throws|,
name|MicroKernelException
block|{
name|String
name|headRevision
init|=
name|mk
operator|.
name|getHeadRevision
argument_list|()
decl_stmt|;
name|String
name|path
init|=
name|request
operator|.
name|getParameter
argument_list|(
literal|"path"
argument_list|,
literal|"/"
argument_list|)
decl_stmt|;
name|String
name|revisionId
init|=
name|request
operator|.
name|getParameter
argument_list|(
literal|"revision_id"
argument_list|,
name|headRevision
argument_list|)
decl_stmt|;
name|response
operator|.
name|setContentType
argument_list|(
literal|"text/plain"
argument_list|)
expr_stmt|;
name|response
operator|.
name|write
argument_list|(
name|Long
operator|.
name|toString
argument_list|(
name|mk
operator|.
name|getChildNodeCount
argument_list|(
name|path
argument_list|,
name|revisionId
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|static
class|class
name|GetNodes
implements|implements
name|Command
block|{
annotation|@
name|Override
specifier|public
name|void
name|execute
parameter_list|(
name|MicroKernel
name|mk
parameter_list|,
name|Request
name|request
parameter_list|,
name|Response
name|response
parameter_list|)
throws|throws
name|IOException
throws|,
name|MicroKernelException
block|{
name|String
name|headRevision
init|=
name|mk
operator|.
name|getHeadRevision
argument_list|()
decl_stmt|;
name|String
name|path
init|=
name|request
operator|.
name|getParameter
argument_list|(
literal|"path"
argument_list|,
literal|"/"
argument_list|)
decl_stmt|;
name|String
name|revisionId
init|=
name|request
operator|.
name|getParameter
argument_list|(
literal|"revision_id"
argument_list|,
name|headRevision
argument_list|)
decl_stmt|;
name|int
name|depth
init|=
name|request
operator|.
name|getParameter
argument_list|(
literal|"depth"
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|long
name|offset
init|=
name|request
operator|.
name|getParameter
argument_list|(
literal|"offset"
argument_list|,
literal|0L
argument_list|)
decl_stmt|;
name|int
name|maxChildNodes
init|=
name|request
operator|.
name|getParameter
argument_list|(
literal|"max_child_nodes"
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
name|String
name|filter
init|=
name|request
operator|.
name|getParameter
argument_list|(
literal|"filter"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|response
operator|.
name|setContentType
argument_list|(
literal|"application/json"
argument_list|)
expr_stmt|;
name|String
name|json
init|=
name|mk
operator|.
name|getNodes
argument_list|(
name|path
argument_list|,
name|revisionId
argument_list|,
name|depth
argument_list|,
name|offset
argument_list|,
name|maxChildNodes
argument_list|,
name|filter
argument_list|)
decl_stmt|;
comment|// OAK-48: MicroKernel.getNodes() should return null for not existing nodes instead of throwing an exception
if|if
condition|(
name|json
operator|==
literal|null
condition|)
block|{
name|json
operator|=
literal|"null"
expr_stmt|;
block|}
if|if
condition|(
name|request
operator|.
name|getHeaders
argument_list|()
operator|.
name|containsKey
argument_list|(
literal|"User-Agent"
argument_list|)
condition|)
block|{
name|json
operator|=
name|JsopBuilder
operator|.
name|prettyPrint
argument_list|(
name|json
argument_list|)
expr_stmt|;
block|}
name|response
operator|.
name|write
argument_list|(
name|json
argument_list|)
expr_stmt|;
block|}
block|}
specifier|static
class|class
name|Commit
implements|implements
name|Command
block|{
annotation|@
name|Override
specifier|public
name|void
name|execute
parameter_list|(
name|MicroKernel
name|mk
parameter_list|,
name|Request
name|request
parameter_list|,
name|Response
name|response
parameter_list|)
throws|throws
name|IOException
throws|,
name|MicroKernelException
block|{
name|String
name|headRevision
init|=
name|mk
operator|.
name|getHeadRevision
argument_list|()
decl_stmt|;
name|String
name|path
init|=
name|request
operator|.
name|getParameter
argument_list|(
literal|"path"
argument_list|,
literal|"/"
argument_list|)
decl_stmt|;
name|String
name|jsonDiff
init|=
name|request
operator|.
name|getParameter
argument_list|(
literal|"json_diff"
argument_list|)
decl_stmt|;
name|String
name|revisionId
init|=
name|request
operator|.
name|getParameter
argument_list|(
literal|"revision_id"
argument_list|,
name|headRevision
argument_list|)
decl_stmt|;
name|String
name|message
init|=
name|request
operator|.
name|getParameter
argument_list|(
literal|"message"
argument_list|)
decl_stmt|;
name|String
name|newRevision
init|=
name|mk
operator|.
name|commit
argument_list|(
name|path
argument_list|,
name|jsonDiff
argument_list|,
name|revisionId
argument_list|,
name|message
argument_list|)
decl_stmt|;
name|response
operator|.
name|setContentType
argument_list|(
literal|"text/plain"
argument_list|)
expr_stmt|;
name|response
operator|.
name|write
argument_list|(
name|newRevision
argument_list|)
expr_stmt|;
block|}
block|}
specifier|static
class|class
name|Branch
implements|implements
name|Command
block|{
annotation|@
name|Override
specifier|public
name|void
name|execute
parameter_list|(
name|MicroKernel
name|mk
parameter_list|,
name|Request
name|request
parameter_list|,
name|Response
name|response
parameter_list|)
throws|throws
name|IOException
throws|,
name|MicroKernelException
block|{
name|String
name|headRevision
init|=
name|mk
operator|.
name|getHeadRevision
argument_list|()
decl_stmt|;
name|String
name|trunkRevisionId
init|=
name|request
operator|.
name|getParameter
argument_list|(
literal|"trunk_revision_id"
argument_list|,
name|headRevision
argument_list|)
decl_stmt|;
name|String
name|newRevision
init|=
name|mk
operator|.
name|branch
argument_list|(
name|trunkRevisionId
argument_list|)
decl_stmt|;
name|response
operator|.
name|setContentType
argument_list|(
literal|"text/plain"
argument_list|)
expr_stmt|;
name|response
operator|.
name|write
argument_list|(
name|newRevision
argument_list|)
expr_stmt|;
block|}
block|}
specifier|static
class|class
name|Merge
implements|implements
name|Command
block|{
annotation|@
name|Override
specifier|public
name|void
name|execute
parameter_list|(
name|MicroKernel
name|mk
parameter_list|,
name|Request
name|request
parameter_list|,
name|Response
name|response
parameter_list|)
throws|throws
name|IOException
throws|,
name|MicroKernelException
block|{
name|String
name|branchRevisionId
init|=
name|request
operator|.
name|getParameter
argument_list|(
literal|"branch_revision_id"
argument_list|)
decl_stmt|;
name|String
name|message
init|=
name|request
operator|.
name|getParameter
argument_list|(
literal|"message"
argument_list|)
decl_stmt|;
name|String
name|newRevision
init|=
name|mk
operator|.
name|merge
argument_list|(
name|branchRevisionId
argument_list|,
name|message
argument_list|)
decl_stmt|;
name|response
operator|.
name|setContentType
argument_list|(
literal|"text/plain"
argument_list|)
expr_stmt|;
name|response
operator|.
name|write
argument_list|(
name|newRevision
argument_list|)
expr_stmt|;
block|}
block|}
specifier|static
class|class
name|GetLength
implements|implements
name|Command
block|{
annotation|@
name|Override
specifier|public
name|void
name|execute
parameter_list|(
name|MicroKernel
name|mk
parameter_list|,
name|Request
name|request
parameter_list|,
name|Response
name|response
parameter_list|)
throws|throws
name|IOException
throws|,
name|MicroKernelException
block|{
name|String
name|blobId
init|=
name|request
operator|.
name|getParameter
argument_list|(
literal|"blob_id"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|long
name|length
init|=
name|mk
operator|.
name|getLength
argument_list|(
name|blobId
argument_list|)
decl_stmt|;
name|response
operator|.
name|setContentType
argument_list|(
literal|"text/plain"
argument_list|)
expr_stmt|;
name|response
operator|.
name|write
argument_list|(
name|Long
operator|.
name|toString
argument_list|(
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|static
class|class
name|Read
implements|implements
name|Command
block|{
annotation|@
name|Override
specifier|public
name|void
name|execute
parameter_list|(
name|MicroKernel
name|mk
parameter_list|,
name|Request
name|request
parameter_list|,
name|Response
name|response
parameter_list|)
throws|throws
name|IOException
throws|,
name|MicroKernelException
block|{
name|String
name|blobId
init|=
name|request
operator|.
name|getParameter
argument_list|(
literal|"blob_id"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|long
name|pos
init|=
name|request
operator|.
name|getParameter
argument_list|(
literal|"pos"
argument_list|,
literal|0L
argument_list|)
decl_stmt|;
name|int
name|length
init|=
name|request
operator|.
name|getParameter
argument_list|(
literal|"length"
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
name|OutputStream
name|out
init|=
name|response
operator|.
name|getOutputStream
argument_list|()
decl_stmt|;
if|if
condition|(
name|pos
operator|==
literal|0L
operator|&&
name|length
operator|==
operator|-
literal|1
condition|)
block|{
comment|/* return the complete binary */
name|InputStream
name|in
init|=
operator|new
name|MicroKernelInputStream
argument_list|(
name|mk
argument_list|,
name|blobId
argument_list|)
decl_stmt|;
name|IOUtils
operator|.
name|copy
argument_list|(
name|in
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|/* return some range */
name|byte
index|[]
name|buff
init|=
operator|new
name|byte
index|[
name|length
index|]
decl_stmt|;
name|int
name|count
init|=
name|mk
operator|.
name|read
argument_list|(
name|blobId
argument_list|,
name|pos
argument_list|,
name|buff
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
decl_stmt|;
if|if
condition|(
name|count
operator|>
literal|0
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
name|buff
argument_list|,
literal|0
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|static
class|class
name|Write
implements|implements
name|Command
block|{
annotation|@
name|Override
specifier|public
name|void
name|execute
parameter_list|(
name|MicroKernel
name|mk
parameter_list|,
name|Request
name|request
parameter_list|,
name|Response
name|response
parameter_list|)
throws|throws
name|IOException
throws|,
name|MicroKernelException
block|{
name|InputStream
name|in
init|=
name|request
operator|.
name|getFileParameter
argument_list|(
literal|"file"
argument_list|)
decl_stmt|;
name|String
name|blobId
init|=
name|mk
operator|.
name|write
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|response
operator|.
name|setContentType
argument_list|(
literal|"text/plain"
argument_list|)
expr_stmt|;
name|response
operator|.
name|write
argument_list|(
name|blobId
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

