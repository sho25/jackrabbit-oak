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
name|plugins
operator|.
name|document
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
name|util
operator|.
name|Iterator
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
name|api
operator|.
name|Blob
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
name|commons
operator|.
name|IOUtils
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
name|blob
operator|.
name|BlobReferenceRetriever
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
name|blob
operator|.
name|BlobStoreBlob
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
name|blob
operator|.
name|ReferenceCollector
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * Implementation of {@link BlobReferenceRetriever} for the DocumentNodeStore.  */
end_comment

begin_class
specifier|public
class|class
name|DocumentBlobReferenceRetriever
implements|implements
name|BlobReferenceRetriever
block|{
specifier|private
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|DocumentNodeStore
name|nodeStore
decl_stmt|;
specifier|public
name|DocumentBlobReferenceRetriever
parameter_list|(
name|DocumentNodeStore
name|nodeStore
parameter_list|)
block|{
name|this
operator|.
name|nodeStore
operator|=
name|nodeStore
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|collectReferences
parameter_list|(
name|ReferenceCollector
name|collector
parameter_list|)
block|{
name|int
name|referencesFound
init|=
literal|0
decl_stmt|;
name|Iterator
argument_list|<
name|Blob
argument_list|>
name|blobIterator
init|=
name|nodeStore
operator|.
name|getReferencedBlobsIterator
argument_list|()
decl_stmt|;
try|try
block|{
while|while
condition|(
name|blobIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Blob
name|blob
init|=
name|blobIterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|referencesFound
operator|++
expr_stmt|;
comment|//TODO this mode would also add in memory blobId
comment|//Would that be an issue
if|if
condition|(
name|blob
operator|instanceof
name|BlobStoreBlob
condition|)
block|{
name|collector
operator|.
name|addReference
argument_list|(
operator|(
operator|(
name|BlobStoreBlob
operator|)
name|blob
operator|)
operator|.
name|getBlobId
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//TODO Should not rely on toString. Instead obtain
comment|//secure reference and convert that to blobId using
comment|//blobStore
name|collector
operator|.
name|addReference
argument_list|(
name|blob
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|blobIterator
operator|instanceof
name|Closeable
condition|)
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
operator|(
name|Closeable
operator|)
name|blobIterator
argument_list|)
expr_stmt|;
block|}
block|}
name|log
operator|.
name|debug
argument_list|(
literal|"Total blob references found (including chunk resolution) [{}]"
argument_list|,
name|referencesFound
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

