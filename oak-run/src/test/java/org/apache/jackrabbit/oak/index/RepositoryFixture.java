begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|index
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

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
name|javax
operator|.
name|jcr
operator|.
name|Repository
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Session
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|SimpleCredentials
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
name|api
operator|.
name|JackrabbitRepository
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
name|oak
operator|.
name|Oak
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
name|oak
operator|.
name|jcr
operator|.
name|Jcr
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
name|oak
operator|.
name|plugins
operator|.
name|index
operator|.
name|AsyncIndexUpdate
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
name|oak
operator|.
name|plugins
operator|.
name|index
operator|.
name|lucene
operator|.
name|LuceneIndexEditorProvider
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
name|oak
operator|.
name|plugins
operator|.
name|index
operator|.
name|lucene
operator|.
name|LuceneIndexProvider
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
name|oak
operator|.
name|segment
operator|.
name|SegmentNodeStoreBuilders
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
name|oak
operator|.
name|segment
operator|.
name|file
operator|.
name|FileStore
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
name|oak
operator|.
name|segment
operator|.
name|file
operator|.
name|FileStoreBuilder
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
name|oak
operator|.
name|segment
operator|.
name|file
operator|.
name|InvalidFileStoreVersionException
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
name|oak
operator|.
name|spi
operator|.
name|commit
operator|.
name|Observer
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
name|oak
operator|.
name|spi
operator|.
name|query
operator|.
name|QueryIndexProvider
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
name|oak
operator|.
name|spi
operator|.
name|state
operator|.
name|NodeStore
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
name|oak
operator|.
name|spi
operator|.
name|whiteboard
operator|.
name|Whiteboard
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
name|oak
operator|.
name|spi
operator|.
name|whiteboard
operator|.
name|WhiteboardUtils
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|spi
operator|.
name|whiteboard
operator|.
name|WhiteboardUtils
operator|.
name|getService
import|;
end_import

begin_class
specifier|public
class|class
name|RepositoryFixture
implements|implements
name|Closeable
block|{
specifier|private
specifier|final
name|File
name|dir
decl_stmt|;
specifier|private
name|Repository
name|repository
decl_stmt|;
specifier|private
name|FileStore
name|fileStore
decl_stmt|;
specifier|private
name|NodeStore
name|nodeStore
decl_stmt|;
specifier|private
name|Whiteboard
name|whiteboard
decl_stmt|;
specifier|public
name|RepositoryFixture
parameter_list|(
name|File
name|dir
parameter_list|)
block|{
name|this
operator|.
name|dir
operator|=
name|dir
expr_stmt|;
block|}
specifier|public
name|Repository
name|getRepository
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|repository
operator|==
literal|null
condition|)
block|{
name|repository
operator|=
name|createRepository
argument_list|()
expr_stmt|;
block|}
return|return
name|repository
return|;
block|}
specifier|public
name|Session
name|getAdminSession
parameter_list|()
throws|throws
name|IOException
throws|,
name|RepositoryException
block|{
return|return
name|getRepository
argument_list|()
operator|.
name|login
argument_list|(
operator|new
name|SimpleCredentials
argument_list|(
literal|"admin"
argument_list|,
literal|"admin"
operator|.
name|toCharArray
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|NodeStore
name|getNodeStore
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|nodeStore
operator|==
literal|null
condition|)
block|{
name|nodeStore
operator|=
name|createNodeStore
argument_list|()
expr_stmt|;
block|}
return|return
name|nodeStore
return|;
block|}
specifier|public
name|AsyncIndexUpdate
name|getAsyncIndexUpdate
parameter_list|(
name|String
name|laneName
parameter_list|)
block|{
return|return
operator|(
name|AsyncIndexUpdate
operator|)
name|getService
argument_list|(
name|whiteboard
argument_list|,
name|Runnable
operator|.
name|class
argument_list|,
parameter_list|(
name|runnable
parameter_list|)
lambda|->
name|runnable
operator|instanceof
name|AsyncIndexUpdate
operator|&&
name|laneName
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|AsyncIndexUpdate
operator|)
name|runnable
operator|)
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|repository
operator|instanceof
name|JackrabbitRepository
condition|)
block|{
operator|(
operator|(
name|JackrabbitRepository
operator|)
name|repository
operator|)
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|repository
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|fileStore
operator|!=
literal|null
condition|)
block|{
name|fileStore
operator|.
name|close
argument_list|()
expr_stmt|;
name|fileStore
operator|=
literal|null
expr_stmt|;
block|}
name|whiteboard
operator|=
literal|null
expr_stmt|;
block|}
specifier|public
name|File
name|getDir
parameter_list|()
block|{
return|return
name|dir
return|;
block|}
specifier|private
name|Repository
name|createRepository
parameter_list|()
throws|throws
name|IOException
block|{
name|Oak
name|oak
init|=
operator|new
name|Oak
argument_list|(
name|getNodeStore
argument_list|()
argument_list|)
decl_stmt|;
name|oak
operator|.
name|withAsyncIndexing
argument_list|(
literal|"async"
argument_list|,
literal|3600
argument_list|)
expr_stmt|;
comment|//Effectively disable async indexing
name|configureLuceneProvider
argument_list|(
name|oak
argument_list|)
expr_stmt|;
name|Jcr
name|jcr
init|=
operator|new
name|Jcr
argument_list|(
name|oak
argument_list|)
decl_stmt|;
name|Repository
name|repository
init|=
name|jcr
operator|.
name|createRepository
argument_list|()
decl_stmt|;
name|whiteboard
operator|=
name|oak
operator|.
name|getWhiteboard
argument_list|()
expr_stmt|;
return|return
name|repository
return|;
block|}
specifier|private
name|void
name|configureLuceneProvider
parameter_list|(
name|Oak
name|oak
parameter_list|)
throws|throws
name|IOException
block|{
name|LuceneIndexEditorProvider
name|ep
init|=
operator|new
name|LuceneIndexEditorProvider
argument_list|()
decl_stmt|;
name|LuceneIndexProvider
name|provider
init|=
operator|new
name|LuceneIndexProvider
argument_list|()
decl_stmt|;
name|oak
operator|.
name|with
argument_list|(
operator|(
name|QueryIndexProvider
operator|)
name|provider
argument_list|)
operator|.
name|with
argument_list|(
operator|(
name|Observer
operator|)
name|provider
argument_list|)
operator|.
name|with
argument_list|(
name|ep
argument_list|)
expr_stmt|;
block|}
specifier|private
name|NodeStore
name|createNodeStore
parameter_list|()
throws|throws
name|IOException
block|{
name|FileStoreBuilder
name|builder
init|=
name|FileStoreBuilder
operator|.
name|fileStoreBuilder
argument_list|(
name|dir
argument_list|)
decl_stmt|;
try|try
block|{
name|fileStore
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidFileStoreVersionException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
name|SegmentNodeStoreBuilders
operator|.
name|builder
argument_list|(
name|fileStore
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
block|}
end_class

end_unit
