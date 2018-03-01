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
operator|.
name|indexer
operator|.
name|document
operator|.
name|flatfile
package|;
end_package

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
name|index
operator|.
name|indexer
operator|.
name|document
operator|.
name|NodeStateEntry
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
name|json
operator|.
name|BlobDeserializer
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
name|json
operator|.
name|JsonDeserializer
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
name|serializer
operator|.
name|BlobIdSerializer
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
name|NodeState
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
name|commons
operator|.
name|StringUtils
operator|.
name|estimateMemoryUsage
import|;
end_import

begin_class
specifier|public
class|class
name|NodeStateEntryReader
block|{
specifier|private
specifier|final
name|BlobDeserializer
name|blobDeserializer
decl_stmt|;
specifier|public
name|NodeStateEntryReader
parameter_list|(
name|BlobStore
name|blobStore
parameter_list|)
block|{
name|this
operator|.
name|blobDeserializer
operator|=
operator|new
name|BlobIdSerializer
argument_list|(
name|blobStore
argument_list|)
expr_stmt|;
block|}
specifier|public
name|NodeStateEntry
name|read
parameter_list|(
name|String
name|line
parameter_list|)
block|{
name|String
index|[]
name|parts
init|=
name|NodeStateEntryWriter
operator|.
name|getParts
argument_list|(
name|line
argument_list|)
decl_stmt|;
name|long
name|memUsage
init|=
name|estimateMemoryUsage
argument_list|(
name|parts
index|[
literal|0
index|]
argument_list|)
operator|+
name|estimateMemoryUsage
argument_list|(
name|parts
index|[
literal|1
index|]
argument_list|)
decl_stmt|;
return|return
operator|new
name|NodeStateEntry
argument_list|(
name|parseState
argument_list|(
name|parts
index|[
literal|1
index|]
argument_list|)
argument_list|,
name|parts
index|[
literal|0
index|]
argument_list|,
name|memUsage
argument_list|)
return|;
block|}
specifier|private
name|NodeState
name|parseState
parameter_list|(
name|String
name|part
parameter_list|)
block|{
name|JsonDeserializer
name|des
init|=
operator|new
name|JsonDeserializer
argument_list|(
name|blobDeserializer
argument_list|)
decl_stmt|;
return|return
name|des
operator|.
name|deserialize
argument_list|(
name|part
argument_list|)
return|;
block|}
block|}
end_class

end_unit

