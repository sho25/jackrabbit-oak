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
name|index
operator|.
name|lucene
operator|.
name|directory
package|;
end_package

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
name|javax
operator|.
name|jcr
operator|.
name|PropertyType
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
name|io
operator|.
name|ByteStreams
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
name|io
operator|.
name|CountingInputStream
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
name|Root
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
name|plugins
operator|.
name|index
operator|.
name|lucene
operator|.
name|LuceneIndexConstants
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
name|tree
operator|.
name|RootFactory
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
name|NodeState
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

begin_class
specifier|public
class|class
name|IndexConsistencyChecker
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
name|NodeState
name|rootState
decl_stmt|;
specifier|private
specifier|final
name|String
name|indexPath
decl_stmt|;
specifier|public
enum|enum
name|Level
block|{
comment|/**          * Consistency check would only check if all blobs referred by index nodes          * are present in BlobStore          */
name|BLOBS_ONLY
block|,
comment|/**          * Performs full check via {@link org.apache.lucene.index.CheckIndex}. This          * reads whole index and hence can take time          */
name|FULL
block|}
specifier|public
specifier|static
class|class
name|Result
block|{
comment|/** True if no problems were found with the index. */
specifier|public
name|boolean
name|clean
decl_stmt|;
specifier|public
name|boolean
name|typeMismatch
decl_stmt|;
specifier|public
name|boolean
name|missingBlobs
decl_stmt|;
specifier|public
name|boolean
name|blobSizeMismatch
decl_stmt|;
specifier|public
name|String
name|indexPath
decl_stmt|;
specifier|public
name|long
name|binaryPropSize
decl_stmt|;
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|invalidBlobIds
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|msgs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
block|}
specifier|public
name|IndexConsistencyChecker
parameter_list|(
name|NodeState
name|rootState
parameter_list|,
name|String
name|indexPath
parameter_list|)
block|{
name|this
operator|.
name|rootState
operator|=
name|rootState
expr_stmt|;
name|this
operator|.
name|indexPath
operator|=
name|indexPath
expr_stmt|;
block|}
specifier|public
name|Result
name|check
parameter_list|(
name|Level
name|level
parameter_list|)
block|{
name|Stopwatch
name|watch
init|=
name|Stopwatch
operator|.
name|createStarted
argument_list|()
decl_stmt|;
name|Result
name|result
init|=
operator|new
name|Result
argument_list|()
decl_stmt|;
name|result
operator|.
name|indexPath
operator|=
name|indexPath
expr_stmt|;
name|result
operator|.
name|clean
operator|=
literal|true
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"[{}] Starting check"
argument_list|,
name|indexPath
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|level
condition|)
block|{
case|case
name|BLOBS_ONLY
case|:
name|checkBlobs
argument_list|(
name|result
argument_list|)
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|result
operator|.
name|clean
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"[] No problems were detected with this index. Time taken {}"
argument_list|,
name|indexPath
argument_list|,
name|watch
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|info
argument_list|(
literal|"[] Problems detected with this index. Time taken {}"
argument_list|,
name|indexPath
argument_list|,
name|watch
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|private
name|void
name|checkBlobs
parameter_list|(
name|Result
name|result
parameter_list|)
block|{
name|Root
name|root
init|=
name|RootFactory
operator|.
name|createReadOnlyRoot
argument_list|(
name|rootState
argument_list|)
decl_stmt|;
name|Tree
name|idx
init|=
name|root
operator|.
name|getTree
argument_list|(
name|indexPath
argument_list|)
decl_stmt|;
name|PropertyState
name|type
init|=
name|idx
operator|.
name|getProperty
argument_list|(
literal|"type"
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|!=
literal|null
operator|&&
name|LuceneIndexConstants
operator|.
name|TYPE_LUCENE
operator|.
name|equals
argument_list|(
name|type
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
condition|)
block|{
name|checkBlobs
argument_list|(
name|result
argument_list|,
name|idx
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|.
name|clean
operator|=
literal|false
expr_stmt|;
name|result
operator|.
name|typeMismatch
operator|=
literal|true
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|checkBlobs
parameter_list|(
name|Result
name|result
parameter_list|,
name|Tree
name|tree
parameter_list|)
block|{
for|for
control|(
name|PropertyState
name|ps
range|:
name|tree
operator|.
name|getProperties
argument_list|()
control|)
block|{
if|if
condition|(
name|ps
operator|.
name|getType
argument_list|()
operator|.
name|tag
argument_list|()
operator|==
name|PropertyType
operator|.
name|BINARY
condition|)
block|{
if|if
condition|(
name|ps
operator|.
name|isArray
argument_list|()
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
name|ps
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
name|ps
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
name|checkBlob
argument_list|(
name|ps
operator|.
name|getName
argument_list|()
argument_list|,
name|b
argument_list|,
name|tree
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|Blob
name|b
init|=
name|ps
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|BINARY
argument_list|)
decl_stmt|;
name|checkBlob
argument_list|(
name|ps
operator|.
name|getName
argument_list|()
argument_list|,
name|b
argument_list|,
name|tree
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
block|}
block|}
for|for
control|(
name|Tree
name|child
range|:
name|tree
operator|.
name|getChildren
argument_list|()
control|)
block|{
name|checkBlobs
argument_list|(
name|result
argument_list|,
name|child
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|checkBlob
parameter_list|(
name|String
name|propName
parameter_list|,
name|Blob
name|blob
parameter_list|,
name|Tree
name|tree
parameter_list|,
name|Result
name|result
parameter_list|)
block|{
name|String
name|id
init|=
name|blob
operator|.
name|getContentIdentity
argument_list|()
decl_stmt|;
name|String
name|blobPath
init|=
name|String
operator|.
name|format
argument_list|(
literal|"%s/%s/%s"
argument_list|,
name|tree
operator|.
name|getPath
argument_list|()
argument_list|,
name|propName
argument_list|,
name|id
argument_list|)
decl_stmt|;
try|try
block|{
name|InputStream
name|is
init|=
name|blob
operator|.
name|getNewStream
argument_list|()
decl_stmt|;
name|CountingInputStream
name|cis
init|=
operator|new
name|CountingInputStream
argument_list|(
name|is
argument_list|)
decl_stmt|;
name|IOUtils
operator|.
name|copyLarge
argument_list|(
name|cis
argument_list|,
name|ByteStreams
operator|.
name|nullOutputStream
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|cis
operator|.
name|getCount
argument_list|()
operator|!=
name|blob
operator|.
name|length
argument_list|()
condition|)
block|{
name|String
name|msg
init|=
name|String
operator|.
name|format
argument_list|(
literal|"Invalid blob %s. Length mismatch - expected ${%d} -> found ${%d}"
argument_list|,
name|blobPath
argument_list|,
name|blob
operator|.
name|length
argument_list|()
argument_list|,
name|cis
operator|.
name|getCount
argument_list|()
argument_list|)
decl_stmt|;
name|result
operator|.
name|msgs
operator|.
name|add
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|result
operator|.
name|invalidBlobIds
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|log
operator|.
name|warn
argument_list|(
literal|"[{}] {}"
argument_list|,
name|indexPath
argument_list|,
name|msg
argument_list|)
expr_stmt|;
name|result
operator|.
name|clean
operator|=
literal|false
expr_stmt|;
name|result
operator|.
name|blobSizeMismatch
operator|=
literal|true
expr_stmt|;
block|}
name|result
operator|.
name|binaryPropSize
operator|+=
name|cis
operator|.
name|getCount
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"[{}] Error occurred reading blob at {}"
argument_list|,
name|indexPath
argument_list|,
name|blobPath
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|result
operator|.
name|invalidBlobIds
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|result
operator|.
name|clean
operator|=
literal|false
expr_stmt|;
name|result
operator|.
name|missingBlobs
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

