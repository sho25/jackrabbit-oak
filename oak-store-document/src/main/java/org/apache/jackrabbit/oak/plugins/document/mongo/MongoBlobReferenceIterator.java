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
operator|.
name|mongo
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterators
operator|.
name|transform
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
name|Collection
operator|.
name|NODES
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
name|plugins
operator|.
name|document
operator|.
name|BlobReferenceIterator
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
name|DocumentNodeStore
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
name|NodeDocument
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
name|CloseableIterator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bson
operator|.
name|conversions
operator|.
name|Bson
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|BasicDBObject
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|client
operator|.
name|MongoCollection
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|client
operator|.
name|MongoCursor
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|client
operator|.
name|model
operator|.
name|Filters
import|;
end_import

begin_class
specifier|public
class|class
name|MongoBlobReferenceIterator
extends|extends
name|BlobReferenceIterator
block|{
specifier|private
specifier|final
name|MongoDocumentStore
name|documentStore
decl_stmt|;
specifier|public
name|MongoBlobReferenceIterator
parameter_list|(
name|DocumentNodeStore
name|nodeStore
parameter_list|,
name|MongoDocumentStore
name|documentStore
parameter_list|)
block|{
name|super
argument_list|(
name|nodeStore
argument_list|)
expr_stmt|;
name|this
operator|.
name|documentStore
operator|=
name|documentStore
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|NodeDocument
argument_list|>
name|getIteratorOverDocsWithBinaries
parameter_list|()
block|{
name|Bson
name|query
init|=
name|Filters
operator|.
name|eq
argument_list|(
name|NodeDocument
operator|.
name|HAS_BINARY_FLAG
argument_list|,
name|NodeDocument
operator|.
name|HAS_BINARY_VAL
argument_list|)
decl_stmt|;
comment|// TODO It currently uses the configured read preference. Would that be Ok?
name|MongoCursor
argument_list|<
name|BasicDBObject
argument_list|>
name|cursor
init|=
name|documentStore
operator|.
name|getDBCollection
argument_list|(
name|NODES
argument_list|)
operator|.
name|find
argument_list|(
name|query
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
return|return
name|CloseableIterator
operator|.
name|wrap
argument_list|(
name|transform
argument_list|(
name|cursor
argument_list|,
name|input
lambda|->
name|documentStore
operator|.
name|convertFromDBObject
argument_list|(
name|NODES
argument_list|,
name|input
argument_list|)
argument_list|)
argument_list|,
name|cursor
argument_list|)
return|;
block|}
block|}
end_class

end_unit

