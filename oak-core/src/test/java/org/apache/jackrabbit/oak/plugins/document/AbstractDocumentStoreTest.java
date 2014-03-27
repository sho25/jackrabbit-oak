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
package|;
end_package

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
name|Collection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
import|;
end_import

begin_class
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
specifier|public
specifier|abstract
class|class
name|AbstractDocumentStoreTest
block|{
specifier|protected
name|String
name|dsname
decl_stmt|;
specifier|protected
name|DocumentStore
name|ds
decl_stmt|;
specifier|public
name|AbstractDocumentStoreTest
parameter_list|(
name|DocumentStoreFixture
name|dsf
parameter_list|)
block|{
name|this
operator|.
name|ds
operator|=
name|dsf
operator|.
name|createDocumentStore
argument_list|()
expr_stmt|;
name|this
operator|.
name|dsname
operator|=
name|dsf
operator|.
name|getName
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Parameterized
operator|.
name|Parameters
specifier|public
specifier|static
name|Collection
argument_list|<
name|Object
index|[]
argument_list|>
name|fixtures
parameter_list|()
block|{
name|Collection
argument_list|<
name|Object
index|[]
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|Object
index|[]
argument_list|>
argument_list|()
decl_stmt|;
name|DocumentStoreFixture
name|candidates
index|[]
init|=
operator|new
name|DocumentStoreFixture
index|[]
block|{
name|DocumentStoreFixture
operator|.
name|MEMORY
block|,
name|DocumentStoreFixture
operator|.
name|MONGO
block|,
name|DocumentStoreFixture
operator|.
name|RDB_H2
block|,
name|DocumentStoreFixture
operator|.
name|RDB_PG
block|,
name|DocumentStoreFixture
operator|.
name|RDB_DB2
block|}
decl_stmt|;
for|for
control|(
name|DocumentStoreFixture
name|dsf
range|:
name|candidates
control|)
block|{
if|if
condition|(
name|dsf
operator|.
name|isAvailable
argument_list|()
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
operator|new
name|Object
index|[]
block|{
name|dsf
block|}
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

