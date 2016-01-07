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
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|PriorityQueue
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
name|base
operator|.
name|Stopwatch
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
name|Iterables
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
name|Lists
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
name|primitives
operator|.
name|Longs
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|DBCollection
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|DBCursor
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|DBObject
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|QueryBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
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
name|commons
operator|.
name|json
operator|.
name|JsopReader
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
name|json
operator|.
name|JsopTokenizer
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
name|document
operator|.
name|mongo
operator|.
name|MongoDocumentStore
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
name|document
operator|.
name|mongo
operator|.
name|MongoDocumentStoreHelper
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
name|document
operator|.
name|util
operator|.
name|Utils
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
name|document
operator|.
name|mongo
operator|.
name|MongoDocumentStoreHelper
operator|.
name|convertFromDBObject
import|;
end_import

begin_comment
comment|/**  * Helper class to access package private method of DocumentNodeStore and other  * classes in this package.  */
end_comment

begin_class
specifier|public
class|class
name|DocumentNodeStoreHelper
block|{
specifier|public
specifier|static
name|void
name|garbageReport
parameter_list|(
name|DocumentNodeStore
name|dns
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"Collecting top 100 nodes with most blob garbage "
argument_list|)
expr_stmt|;
name|Stopwatch
name|sw
init|=
name|Stopwatch
operator|.
name|createStarted
argument_list|()
decl_stmt|;
name|Iterable
argument_list|<
name|BlobReferences
argument_list|>
name|refs
init|=
name|scan
argument_list|(
name|dns
argument_list|,
operator|new
name|BlobGarbageSizeComparator
argument_list|()
argument_list|,
literal|100
argument_list|)
decl_stmt|;
for|for
control|(
name|BlobReferences
name|br
range|:
name|refs
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|br
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Collected in "
operator|+
name|sw
operator|.
name|stop
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|Iterable
argument_list|<
name|BlobReferences
argument_list|>
name|scan
parameter_list|(
name|DocumentNodeStore
name|store
parameter_list|,
name|Comparator
argument_list|<
name|BlobReferences
argument_list|>
name|comparator
parameter_list|,
name|int
name|num
parameter_list|)
block|{
name|long
name|totalGarbage
init|=
literal|0
decl_stmt|;
name|Iterable
argument_list|<
name|NodeDocument
argument_list|>
name|docs
init|=
name|getDocuments
argument_list|(
name|store
operator|.
name|getDocumentStore
argument_list|()
argument_list|)
decl_stmt|;
name|PriorityQueue
argument_list|<
name|BlobReferences
argument_list|>
name|queue
init|=
operator|new
name|PriorityQueue
argument_list|<
name|BlobReferences
argument_list|>
argument_list|(
name|num
argument_list|,
name|comparator
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Blob
argument_list|>
name|blobs
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|long
name|docCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|NodeDocument
name|doc
range|:
name|docs
control|)
block|{
if|if
condition|(
operator|++
name|docCount
operator|%
literal|10000
operator|==
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"."
argument_list|)
expr_stmt|;
block|}
name|blobs
operator|.
name|clear
argument_list|()
expr_stmt|;
name|BlobReferences
name|refs
init|=
name|collectReferences
argument_list|(
name|doc
argument_list|,
name|store
argument_list|)
decl_stmt|;
name|totalGarbage
operator|+=
name|refs
operator|.
name|garbageSize
expr_stmt|;
name|queue
operator|.
name|add
argument_list|(
name|refs
argument_list|)
expr_stmt|;
if|if
condition|(
name|queue
operator|.
name|size
argument_list|()
operator|>
name|num
condition|)
block|{
name|queue
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|BlobReferences
argument_list|>
name|refs
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|refs
operator|.
name|addAll
argument_list|(
name|queue
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|refs
argument_list|,
name|Collections
operator|.
name|reverseOrder
argument_list|(
name|comparator
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Total garbage size: "
operator|+
name|FileUtils
operator|.
name|byteCountToDisplaySize
argument_list|(
name|totalGarbage
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Total number of nodes with blob references: "
operator|+
name|docCount
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"total referenced / old referenced / # blob references / path"
argument_list|)
expr_stmt|;
return|return
name|refs
return|;
block|}
specifier|private
specifier|static
name|BlobReferences
name|collectReferences
parameter_list|(
name|NodeDocument
name|doc
parameter_list|,
name|DocumentNodeStore
name|ns
parameter_list|)
block|{
name|long
name|blobSize
init|=
literal|0
decl_stmt|;
name|long
name|garbageSize
init|=
literal|0
decl_stmt|;
name|int
name|numBlobs
init|=
literal|0
decl_stmt|;
name|List
argument_list|<
name|Blob
argument_list|>
name|blobs
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|RevisionVector
name|head
init|=
name|ns
operator|.
name|getHeadRevision
argument_list|()
decl_stmt|;
name|boolean
name|exists
init|=
name|doc
operator|.
name|getNodeAtRevision
argument_list|(
name|ns
argument_list|,
name|head
argument_list|,
literal|null
argument_list|)
operator|!=
literal|null
decl_stmt|;
for|for
control|(
name|String
name|key
range|:
name|doc
operator|.
name|keySet
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|Utils
operator|.
name|isPropertyName
argument_list|(
name|key
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|boolean
name|foundValid
init|=
literal|false
decl_stmt|;
name|Map
argument_list|<
name|Revision
argument_list|,
name|String
argument_list|>
name|valueMap
init|=
name|doc
operator|.
name|getLocalMap
argument_list|(
name|key
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Revision
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|valueMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|blobs
operator|.
name|clear
argument_list|()
expr_stmt|;
name|String
name|v
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|v
operator|!=
literal|null
condition|)
block|{
name|loadValue
argument_list|(
name|v
argument_list|,
name|blobs
argument_list|,
name|ns
argument_list|)
expr_stmt|;
block|}
name|blobSize
operator|+=
name|size
argument_list|(
name|blobs
argument_list|)
expr_stmt|;
if|if
condition|(
name|foundValid
condition|)
block|{
name|garbageSize
operator|+=
name|size
argument_list|(
name|blobs
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|doc
operator|.
name|isCommitted
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
name|foundValid
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|garbageSize
operator|+=
name|size
argument_list|(
name|blobs
argument_list|)
expr_stmt|;
block|}
name|numBlobs
operator|+=
name|blobs
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
block|}
return|return
operator|new
name|BlobReferences
argument_list|(
name|doc
operator|.
name|getPath
argument_list|()
argument_list|,
name|blobSize
argument_list|,
name|numBlobs
argument_list|,
name|garbageSize
argument_list|,
name|exists
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|Iterable
argument_list|<
name|NodeDocument
argument_list|>
name|getDocuments
parameter_list|(
name|DocumentStore
name|store
parameter_list|)
block|{
if|if
condition|(
name|store
operator|instanceof
name|MongoDocumentStore
condition|)
block|{
comment|// optimized implementation for MongoDocumentStore
specifier|final
name|MongoDocumentStore
name|mds
init|=
operator|(
name|MongoDocumentStore
operator|)
name|store
decl_stmt|;
name|DBCollection
name|dbCol
init|=
name|MongoDocumentStoreHelper
operator|.
name|getDBCollection
argument_list|(
name|mds
argument_list|,
name|Collection
operator|.
name|NODES
argument_list|)
decl_stmt|;
name|DBObject
name|query
init|=
name|QueryBuilder
operator|.
name|start
argument_list|(
name|NodeDocument
operator|.
name|HAS_BINARY_FLAG
argument_list|)
operator|.
name|is
argument_list|(
name|NodeDocument
operator|.
name|HAS_BINARY_VAL
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|DBCursor
name|cursor
init|=
name|dbCol
operator|.
name|find
argument_list|(
name|query
argument_list|)
decl_stmt|;
return|return
name|Iterables
operator|.
name|transform
argument_list|(
name|cursor
argument_list|,
operator|new
name|Function
argument_list|<
name|DBObject
argument_list|,
name|NodeDocument
argument_list|>
argument_list|()
block|{
annotation|@
name|Nullable
annotation|@
name|Override
specifier|public
name|NodeDocument
name|apply
parameter_list|(
name|DBObject
name|input
parameter_list|)
block|{
return|return
name|convertFromDBObject
argument_list|(
name|mds
argument_list|,
name|Collection
operator|.
name|NODES
argument_list|,
name|input
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|Utils
operator|.
name|getSelectedDocuments
argument_list|(
name|store
argument_list|,
name|NodeDocument
operator|.
name|HAS_BINARY_FLAG
argument_list|,
name|NodeDocument
operator|.
name|HAS_BINARY_VAL
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|static
name|long
name|size
parameter_list|(
name|Iterable
argument_list|<
name|Blob
argument_list|>
name|blobs
parameter_list|)
block|{
name|long
name|size
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Blob
name|b
range|:
name|blobs
control|)
block|{
name|size
operator|+=
name|b
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
return|return
name|size
return|;
block|}
specifier|private
specifier|static
name|void
name|loadValue
parameter_list|(
name|String
name|v
parameter_list|,
name|java
operator|.
name|util
operator|.
name|Collection
argument_list|<
name|Blob
argument_list|>
name|blobs
parameter_list|,
name|DocumentNodeStore
name|nodeStore
parameter_list|)
block|{
name|JsopReader
name|reader
init|=
operator|new
name|JsopTokenizer
argument_list|(
name|v
argument_list|)
decl_stmt|;
name|PropertyState
name|p
decl_stmt|;
if|if
condition|(
name|reader
operator|.
name|matches
argument_list|(
literal|'['
argument_list|)
condition|)
block|{
name|p
operator|=
name|DocumentPropertyState
operator|.
name|readArrayProperty
argument_list|(
literal|"x"
argument_list|,
name|nodeStore
argument_list|,
name|reader
argument_list|)
expr_stmt|;
if|if
condition|(
name|p
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|BINARIES
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|p
operator|.
name|count
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Blob
name|b
init|=
name|p
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|BINARY
argument_list|,
name|i
argument_list|)
decl_stmt|;
name|blobs
operator|.
name|add
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|p
operator|=
name|DocumentPropertyState
operator|.
name|readProperty
argument_list|(
literal|"x"
argument_list|,
name|nodeStore
argument_list|,
name|reader
argument_list|)
expr_stmt|;
if|if
condition|(
name|p
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|BINARY
condition|)
block|{
name|Blob
name|b
init|=
name|p
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|BINARY
argument_list|)
decl_stmt|;
name|blobs
operator|.
name|add
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
specifier|static
class|class
name|BlobReferences
block|{
specifier|final
name|String
name|path
decl_stmt|;
specifier|final
name|long
name|blobSize
decl_stmt|;
specifier|final
name|long
name|garbageSize
decl_stmt|;
specifier|final
name|int
name|numBlobs
decl_stmt|;
specifier|final
name|boolean
name|exists
decl_stmt|;
specifier|public
name|BlobReferences
parameter_list|(
name|String
name|path
parameter_list|,
name|long
name|blobSize
parameter_list|,
name|int
name|numBlobs
parameter_list|,
name|long
name|garbageSize
parameter_list|,
name|boolean
name|exists
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|blobSize
operator|=
name|blobSize
expr_stmt|;
name|this
operator|.
name|garbageSize
operator|=
name|garbageSize
expr_stmt|;
name|this
operator|.
name|numBlobs
operator|=
name|numBlobs
expr_stmt|;
name|this
operator|.
name|exists
operator|=
name|exists
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|String
name|s
init|=
name|FileUtils
operator|.
name|byteCountToDisplaySize
argument_list|(
name|blobSize
argument_list|)
operator|+
literal|"\t"
operator|+
name|FileUtils
operator|.
name|byteCountToDisplaySize
argument_list|(
name|garbageSize
argument_list|)
operator|+
literal|"\t"
operator|+
name|numBlobs
operator|+
literal|"\t"
operator|+
name|path
decl_stmt|;
if|if
condition|(
operator|!
name|exists
condition|)
block|{
name|s
operator|+=
literal|"\t(deleted)"
expr_stmt|;
block|}
return|return
name|s
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|BlobGarbageSizeComparator
implements|implements
name|Comparator
argument_list|<
name|BlobReferences
argument_list|>
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|BlobReferences
name|o1
parameter_list|,
name|BlobReferences
name|o2
parameter_list|)
block|{
name|int
name|c
init|=
name|Longs
operator|.
name|compare
argument_list|(
name|o1
operator|.
name|garbageSize
argument_list|,
name|o2
operator|.
name|garbageSize
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|!=
literal|0
condition|)
block|{
return|return
name|c
return|;
block|}
return|return
name|o1
operator|.
name|path
operator|.
name|compareTo
argument_list|(
name|o2
operator|.
name|path
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

