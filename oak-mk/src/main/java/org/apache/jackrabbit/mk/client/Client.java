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
name|client
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
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|Socket
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|SocketFactory
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
name|util
operator|.
name|IOUtils
import|;
end_import

begin_comment
comment|/**  * Client exposing a {@code MicroKernel} interface, that "remotes" commands  * to a server.  */
end_comment

begin_class
specifier|public
class|class
name|Client
implements|implements
name|MicroKernel
block|{
specifier|private
specifier|static
specifier|final
name|String
name|MK_EXCEPTION_PREFIX
init|=
name|MicroKernelException
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|":"
decl_stmt|;
specifier|private
specifier|final
name|InetSocketAddress
name|addr
decl_stmt|;
specifier|private
specifier|final
name|SocketFactory
name|socketFactory
decl_stmt|;
specifier|private
specifier|final
name|AtomicBoolean
name|disposed
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
specifier|private
name|HttpExecutor
name|executor
decl_stmt|;
comment|/**      * Returns the socket address of the given URL.      *       * @param url URL      * @return socket address      */
specifier|private
specifier|static
name|InetSocketAddress
name|getAddress
parameter_list|(
name|String
name|url
parameter_list|)
block|{
try|try
block|{
name|URI
name|uri
init|=
operator|new
name|URI
argument_list|(
name|url
argument_list|)
decl_stmt|;
return|return
operator|new
name|InetSocketAddress
argument_list|(
name|uri
operator|.
name|getHost
argument_list|()
argument_list|,
name|uri
operator|.
name|getPort
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * Create a new instance of this class.      *       * @param url socket address      */
specifier|public
name|Client
parameter_list|(
name|String
name|url
parameter_list|)
block|{
name|this
argument_list|(
name|getAddress
argument_list|(
name|url
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Create a new instance of this class.      *       * @param addr socket address      */
specifier|public
name|Client
parameter_list|(
name|InetSocketAddress
name|addr
parameter_list|)
block|{
name|this
argument_list|(
name|addr
argument_list|,
name|SocketFactory
operator|.
name|getDefault
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Create a new instance of this class.      *       * @param addr socket address      */
specifier|public
name|Client
parameter_list|(
name|InetSocketAddress
name|addr
parameter_list|,
name|SocketFactory
name|socketFactory
parameter_list|)
block|{
name|this
operator|.
name|addr
operator|=
name|addr
expr_stmt|;
name|this
operator|.
name|socketFactory
operator|=
name|socketFactory
expr_stmt|;
block|}
comment|//-------------------------------------------------- implements MicroKernel
annotation|@
name|Override
specifier|public
name|void
name|dispose
parameter_list|()
block|{
comment|// do nothing
block|}
annotation|@
name|Override
specifier|public
name|String
name|getHeadRevision
parameter_list|()
throws|throws
name|MicroKernelException
block|{
name|Request
name|request
init|=
literal|null
decl_stmt|;
try|try
block|{
name|request
operator|=
name|createRequest
argument_list|(
literal|"getHeadRevision"
argument_list|)
expr_stmt|;
return|return
name|request
operator|.
name|getString
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
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
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|getRevisionHistory
parameter_list|(
name|long
name|since
parameter_list|,
name|int
name|maxEntries
parameter_list|)
throws|throws
name|MicroKernelException
block|{
name|Request
name|request
init|=
literal|null
decl_stmt|;
try|try
block|{
name|request
operator|=
name|createRequest
argument_list|(
literal|"getRevisionHistory"
argument_list|)
expr_stmt|;
name|request
operator|.
name|addParameter
argument_list|(
literal|"since"
argument_list|,
name|since
argument_list|)
expr_stmt|;
name|request
operator|.
name|addParameter
argument_list|(
literal|"max_entries"
argument_list|,
name|maxEntries
argument_list|)
expr_stmt|;
return|return
name|request
operator|.
name|getString
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
name|toMicroKernelException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|waitForCommit
parameter_list|(
name|String
name|oldHeadRevisionId
parameter_list|,
name|long
name|maxWaitMillis
parameter_list|)
throws|throws
name|MicroKernelException
throws|,
name|InterruptedException
block|{
name|Request
name|request
init|=
literal|null
decl_stmt|;
try|try
block|{
name|request
operator|=
name|createRequest
argument_list|(
literal|"waitForCommit"
argument_list|)
expr_stmt|;
name|request
operator|.
name|addParameter
argument_list|(
literal|"revision_id"
argument_list|,
name|oldHeadRevisionId
argument_list|)
expr_stmt|;
name|request
operator|.
name|addParameter
argument_list|(
literal|"max_wait_millis"
argument_list|,
name|maxWaitMillis
argument_list|)
expr_stmt|;
return|return
name|request
operator|.
name|getString
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
name|toMicroKernelException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|getJournal
parameter_list|(
name|String
name|fromRevisionId
parameter_list|,
name|String
name|toRevisionId
parameter_list|,
name|String
name|filter
parameter_list|)
throws|throws
name|MicroKernelException
block|{
name|Request
name|request
init|=
literal|null
decl_stmt|;
try|try
block|{
name|request
operator|=
name|createRequest
argument_list|(
literal|"getJournal"
argument_list|)
expr_stmt|;
name|request
operator|.
name|addParameter
argument_list|(
literal|"from_revision_id"
argument_list|,
name|fromRevisionId
argument_list|)
expr_stmt|;
name|request
operator|.
name|addParameter
argument_list|(
literal|"to_revision_id"
argument_list|,
name|toRevisionId
argument_list|)
expr_stmt|;
name|request
operator|.
name|addParameter
argument_list|(
literal|"filter"
argument_list|,
name|filter
argument_list|)
expr_stmt|;
return|return
name|request
operator|.
name|getString
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
name|toMicroKernelException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|diff
parameter_list|(
name|String
name|fromRevisionId
parameter_list|,
name|String
name|toRevisionId
parameter_list|,
name|String
name|filter
parameter_list|)
throws|throws
name|MicroKernelException
block|{
name|Request
name|request
init|=
literal|null
decl_stmt|;
try|try
block|{
name|request
operator|=
name|createRequest
argument_list|(
literal|"diff"
argument_list|)
expr_stmt|;
name|request
operator|.
name|addParameter
argument_list|(
literal|"from_revision_id"
argument_list|,
name|fromRevisionId
argument_list|)
expr_stmt|;
name|request
operator|.
name|addParameter
argument_list|(
literal|"to_revision_id"
argument_list|,
name|toRevisionId
argument_list|)
expr_stmt|;
name|request
operator|.
name|addParameter
argument_list|(
literal|"filter"
argument_list|,
name|filter
argument_list|)
expr_stmt|;
return|return
name|request
operator|.
name|getString
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
name|toMicroKernelException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|nodeExists
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|revisionId
parameter_list|)
throws|throws
name|MicroKernelException
block|{
name|Request
name|request
init|=
literal|null
decl_stmt|;
try|try
block|{
name|request
operator|=
name|createRequest
argument_list|(
literal|"nodeExists"
argument_list|)
expr_stmt|;
name|request
operator|.
name|addParameter
argument_list|(
literal|"path"
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|request
operator|.
name|addParameter
argument_list|(
literal|"revision_id"
argument_list|,
name|revisionId
argument_list|)
expr_stmt|;
return|return
name|request
operator|.
name|getBoolean
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
name|toMicroKernelException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|long
name|getChildNodeCount
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|revisionId
parameter_list|)
throws|throws
name|MicroKernelException
block|{
name|Request
name|request
init|=
literal|null
decl_stmt|;
try|try
block|{
name|request
operator|=
name|createRequest
argument_list|(
literal|"getChildNodeCount"
argument_list|)
expr_stmt|;
name|request
operator|.
name|addParameter
argument_list|(
literal|"path"
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|request
operator|.
name|addParameter
argument_list|(
literal|"revision_id"
argument_list|,
name|revisionId
argument_list|)
expr_stmt|;
return|return
name|request
operator|.
name|getLong
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
name|toMicroKernelException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|getNodes
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|revisionId
parameter_list|)
throws|throws
name|MicroKernelException
block|{
return|return
name|getNodes
argument_list|(
name|path
argument_list|,
name|revisionId
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getNodes
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|revisionId
parameter_list|,
name|int
name|depth
parameter_list|,
name|long
name|offset
parameter_list|,
name|int
name|count
parameter_list|,
name|String
name|filter
parameter_list|)
throws|throws
name|MicroKernelException
block|{
name|Request
name|request
init|=
literal|null
decl_stmt|;
try|try
block|{
name|request
operator|=
name|createRequest
argument_list|(
literal|"getNodes"
argument_list|)
expr_stmt|;
name|request
operator|.
name|addParameter
argument_list|(
literal|"path"
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|request
operator|.
name|addParameter
argument_list|(
literal|"revision_id"
argument_list|,
name|revisionId
argument_list|)
expr_stmt|;
name|request
operator|.
name|addParameter
argument_list|(
literal|"depth"
argument_list|,
name|depth
argument_list|)
expr_stmt|;
name|request
operator|.
name|addParameter
argument_list|(
literal|"offset"
argument_list|,
name|offset
argument_list|)
expr_stmt|;
name|request
operator|.
name|addParameter
argument_list|(
literal|"count"
argument_list|,
name|count
argument_list|)
expr_stmt|;
name|request
operator|.
name|addParameter
argument_list|(
literal|"filter"
argument_list|,
name|filter
argument_list|)
expr_stmt|;
comment|// OAK-48: MicroKernel.getNodes() should return null for not existing nodes instead of throwing an exception
name|String
name|result
init|=
name|request
operator|.
name|getString
argument_list|()
decl_stmt|;
return|return
name|result
operator|.
name|equals
argument_list|(
literal|"null"
argument_list|)
condition|?
literal|null
else|:
name|result
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
name|toMicroKernelException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|commit
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|jsonDiff
parameter_list|,
name|String
name|revisionId
parameter_list|,
name|String
name|message
parameter_list|)
throws|throws
name|MicroKernelException
block|{
name|Request
name|request
init|=
literal|null
decl_stmt|;
try|try
block|{
name|request
operator|=
name|createRequest
argument_list|(
literal|"commit"
argument_list|)
expr_stmt|;
name|request
operator|.
name|addParameter
argument_list|(
literal|"path"
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|request
operator|.
name|addParameter
argument_list|(
literal|"json_diff"
argument_list|,
name|jsonDiff
argument_list|)
expr_stmt|;
name|request
operator|.
name|addParameter
argument_list|(
literal|"revision_id"
argument_list|,
name|revisionId
argument_list|)
expr_stmt|;
name|request
operator|.
name|addParameter
argument_list|(
literal|"message"
argument_list|,
name|message
argument_list|)
expr_stmt|;
return|return
name|request
operator|.
name|getString
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
name|toMicroKernelException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|branch
parameter_list|(
name|String
name|trunkRevisionId
parameter_list|)
throws|throws
name|MicroKernelException
block|{
name|Request
name|request
init|=
literal|null
decl_stmt|;
try|try
block|{
name|request
operator|=
name|createRequest
argument_list|(
literal|"branch"
argument_list|)
expr_stmt|;
name|request
operator|.
name|addParameter
argument_list|(
literal|"trunk_revision_id"
argument_list|,
name|trunkRevisionId
argument_list|)
expr_stmt|;
return|return
name|request
operator|.
name|getString
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
name|toMicroKernelException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|merge
parameter_list|(
name|String
name|branchRevisionId
parameter_list|,
name|String
name|message
parameter_list|)
throws|throws
name|MicroKernelException
block|{
name|Request
name|request
init|=
literal|null
decl_stmt|;
try|try
block|{
name|request
operator|=
name|createRequest
argument_list|(
literal|"merge"
argument_list|)
expr_stmt|;
name|request
operator|.
name|addParameter
argument_list|(
literal|"branch_revision_id"
argument_list|,
name|branchRevisionId
argument_list|)
expr_stmt|;
name|request
operator|.
name|addParameter
argument_list|(
literal|"message"
argument_list|,
name|message
argument_list|)
expr_stmt|;
return|return
name|request
operator|.
name|getString
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
name|toMicroKernelException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|long
name|getLength
parameter_list|(
name|String
name|blobId
parameter_list|)
throws|throws
name|MicroKernelException
block|{
name|Request
name|request
init|=
literal|null
decl_stmt|;
try|try
block|{
name|request
operator|=
name|createRequest
argument_list|(
literal|"getLength"
argument_list|)
expr_stmt|;
name|request
operator|.
name|addParameter
argument_list|(
literal|"blob_id"
argument_list|,
name|blobId
argument_list|)
expr_stmt|;
return|return
name|request
operator|.
name|getLong
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
name|toMicroKernelException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|int
name|read
parameter_list|(
name|String
name|blobId
parameter_list|,
name|long
name|pos
parameter_list|,
name|byte
index|[]
name|buff
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|MicroKernelException
block|{
name|Request
name|request
init|=
literal|null
decl_stmt|;
try|try
block|{
name|request
operator|=
name|createRequest
argument_list|(
literal|"read"
argument_list|)
expr_stmt|;
name|request
operator|.
name|addParameter
argument_list|(
literal|"blob_id"
argument_list|,
name|blobId
argument_list|)
expr_stmt|;
name|request
operator|.
name|addParameter
argument_list|(
literal|"pos"
argument_list|,
name|pos
argument_list|)
expr_stmt|;
name|request
operator|.
name|addParameter
argument_list|(
literal|"length"
argument_list|,
name|length
argument_list|)
expr_stmt|;
return|return
name|request
operator|.
name|read
argument_list|(
name|buff
argument_list|,
name|off
argument_list|,
name|length
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
name|toMicroKernelException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|write
parameter_list|(
name|InputStream
name|in
parameter_list|)
throws|throws
name|MicroKernelException
block|{
name|Request
name|request
init|=
literal|null
decl_stmt|;
try|try
block|{
name|request
operator|=
name|createRequest
argument_list|(
literal|"write"
argument_list|)
expr_stmt|;
name|request
operator|.
name|addFileParameter
argument_list|(
literal|"file"
argument_list|,
name|in
argument_list|)
expr_stmt|;
return|return
name|request
operator|.
name|getString
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
name|toMicroKernelException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Convert an I/O exception into a MicroKernelException, possibly by       * unwrapping an already wrapped MicroKernelException.      *       * @param e I/O exception       * @return MicroKernelException      */
specifier|private
name|MicroKernelException
name|toMicroKernelException
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|String
name|msg
init|=
name|e
operator|.
name|getMessage
argument_list|()
decl_stmt|;
if|if
condition|(
name|msg
operator|!=
literal|null
operator|&&
name|msg
operator|.
name|startsWith
argument_list|(
name|MK_EXCEPTION_PREFIX
argument_list|)
condition|)
block|{
return|return
operator|new
name|MicroKernelException
argument_list|(
name|msg
operator|.
name|substring
argument_list|(
name|MK_EXCEPTION_PREFIX
operator|.
name|length
argument_list|()
argument_list|)
operator|.
name|trim
argument_list|()
argument_list|)
return|;
block|}
return|return
operator|new
name|MicroKernelException
argument_list|(
name|e
argument_list|)
return|;
block|}
comment|/**      * Create a request for the given command to be executed.      *       * @param command command name      * @return request      * @throws IOException if an I/O error occurs      * @throws MicroKernelException if an exception occurs      */
specifier|private
name|Request
name|createRequest
parameter_list|(
name|String
name|command
parameter_list|)
throws|throws
name|IOException
throws|,
name|MicroKernelException
block|{
return|return
operator|new
name|Request
argument_list|(
name|socketFactory
argument_list|,
name|addr
argument_list|,
name|command
argument_list|)
return|;
block|}
block|}
end_class

end_unit

