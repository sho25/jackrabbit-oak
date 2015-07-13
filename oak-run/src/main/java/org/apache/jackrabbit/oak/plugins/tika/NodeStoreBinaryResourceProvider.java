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
name|tika
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|CheckForNull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Function
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|FluentIterable
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|TreeTraverser
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
name|JcrConstants
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
name|api
operator|.
name|PropertyState
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
name|Tree
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
name|Type
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
name|blob
operator|.
name|BlobStore
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

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Predicates
operator|.
name|notNull
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
name|plugins
operator|.
name|tree
operator|.
name|TreeFactory
operator|.
name|createReadOnlyTree
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
name|state
operator|.
name|NodeStateUtils
operator|.
name|getNode
import|;
end_import

begin_class
class|class
name|NodeStoreBinaryResourceProvider
implements|implements
name|BinaryResourceProvider
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|NodeStoreBinaryResourceProvider
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|NodeStore
name|nodeStore
decl_stmt|;
specifier|private
specifier|final
name|BlobStore
name|blobStore
decl_stmt|;
specifier|public
name|NodeStoreBinaryResourceProvider
parameter_list|(
name|NodeStore
name|nodeStore
parameter_list|,
name|BlobStore
name|blobStore
parameter_list|)
block|{
name|this
operator|.
name|nodeStore
operator|=
name|nodeStore
expr_stmt|;
name|this
operator|.
name|blobStore
operator|=
name|blobStore
expr_stmt|;
block|}
specifier|public
name|FluentIterable
argument_list|<
name|BinaryResource
argument_list|>
name|getBinaries
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
operator|new
name|OakTreeTraverser
argument_list|()
operator|.
name|preOrderTraversal
argument_list|(
name|createReadOnlyTree
argument_list|(
name|getNode
argument_list|(
name|nodeStore
operator|.
name|getRoot
argument_list|()
argument_list|,
name|path
argument_list|)
argument_list|)
argument_list|)
operator|.
name|transform
argument_list|(
operator|new
name|TreeToBinarySource
argument_list|()
argument_list|)
operator|.
name|filter
argument_list|(
name|notNull
argument_list|()
argument_list|)
return|;
block|}
specifier|private
class|class
name|TreeToBinarySource
implements|implements
name|Function
argument_list|<
name|Tree
argument_list|,
name|BinaryResource
argument_list|>
block|{
annotation|@
name|Nullable
annotation|@
name|Override
specifier|public
name|BinaryResource
name|apply
parameter_list|(
name|Tree
name|tree
parameter_list|)
block|{
name|PropertyState
name|data
init|=
name|tree
operator|.
name|getProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_DATA
argument_list|)
decl_stmt|;
if|if
condition|(
name|data
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|data
operator|.
name|isArray
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Ignoring jcr:data property at {} as its a MVP"
argument_list|,
name|tree
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
name|Blob
name|blob
init|=
name|data
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|BINARY
argument_list|)
decl_stmt|;
name|String
name|blobId
init|=
name|blob
operator|.
name|getContentIdentity
argument_list|()
decl_stmt|;
comment|//Check for ref being non null to ensure its not an inlined binary
comment|//For Segment ContentIdentity defaults to RecordId
if|if
condition|(
name|blob
operator|.
name|getReference
argument_list|()
operator|==
literal|null
operator|||
name|blobId
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Ignoring jcr:data property at {} as its an inlined blob"
argument_list|,
name|tree
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
name|String
name|mimeType
init|=
name|getString
argument_list|(
name|tree
argument_list|,
name|JcrConstants
operator|.
name|JCR_MIMETYPE
argument_list|)
decl_stmt|;
name|String
name|encoding
init|=
name|getString
argument_list|(
name|tree
argument_list|,
name|JcrConstants
operator|.
name|JCR_ENCODING
argument_list|)
decl_stmt|;
return|return
operator|new
name|BinaryResource
argument_list|(
operator|new
name|BlobStoreByteSource
argument_list|(
name|blobStore
argument_list|,
name|blobId
argument_list|)
argument_list|,
name|mimeType
argument_list|,
name|encoding
argument_list|,
name|tree
operator|.
name|getPath
argument_list|()
argument_list|,
name|blobId
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|OakTreeTraverser
extends|extends
name|TreeTraverser
argument_list|<
name|Tree
argument_list|>
block|{
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|Tree
argument_list|>
name|children
parameter_list|(
name|Tree
name|root
parameter_list|)
block|{
return|return
name|root
operator|.
name|getChildren
argument_list|()
return|;
block|}
block|}
annotation|@
name|CheckForNull
specifier|private
specifier|static
name|String
name|getString
parameter_list|(
name|Tree
name|tree
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|PropertyState
name|prop
init|=
name|tree
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
decl_stmt|;
return|return
name|prop
operator|!=
literal|null
condition|?
name|prop
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
else|:
literal|null
return|;
block|}
block|}
end_class

end_unit

