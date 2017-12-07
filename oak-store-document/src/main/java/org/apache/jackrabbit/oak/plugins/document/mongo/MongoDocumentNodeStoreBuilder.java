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
name|oak
operator|.
name|plugins
operator|.
name|document
operator|.
name|mongo
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
name|plugins
operator|.
name|document
operator|.
name|DocumentNodeStore
import|;
end_import

begin_comment
comment|/**  * A builder for a {@link DocumentNodeStore} backed by MongoDB.  */
end_comment

begin_class
specifier|public
class|class
name|MongoDocumentNodeStoreBuilder
extends|extends
name|MongoDocumentNodeStoreBuilderBase
argument_list|<
name|MongoDocumentNodeStoreBuilder
argument_list|>
block|{
comment|/**      * @return a new {@link MongoDocumentNodeStoreBuilder}.      */
specifier|public
specifier|static
name|MongoDocumentNodeStoreBuilder
name|newMongoDocumentNodeStoreBuilder
parameter_list|()
block|{
return|return
operator|new
name|MongoDocumentNodeStoreBuilder
argument_list|()
return|;
block|}
block|}
end_class

end_unit

