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
name|rdb
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
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
name|Arrays
import|;
end_import

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
name|List
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
name|AbstractDocumentStoreTest
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
name|Collection
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
name|DocumentStoreFixture
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
name|UpdateOp
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
name|rdb
operator|.
name|RDBDocumentStore
operator|.
name|QueryCondition
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
specifier|public
class|class
name|RDBDocumentStoreTest
extends|extends
name|AbstractDocumentStoreTest
block|{
specifier|public
name|RDBDocumentStoreTest
parameter_list|(
name|DocumentStoreFixture
name|dsf
parameter_list|)
block|{
name|super
argument_list|(
name|dsf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRDBQueryConditions
parameter_list|()
block|{
if|if
condition|(
name|ds
operator|instanceof
name|RDBDocumentStore
condition|)
block|{
name|RDBDocumentStore
name|rds
init|=
operator|(
name|RDBDocumentStore
operator|)
name|ds
decl_stmt|;
comment|// create ten documents
name|long
name|now
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|String
name|base
init|=
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|".testRDBQuery-"
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|String
name|id
init|=
name|base
operator|+
name|i
decl_stmt|;
name|UpdateOp
name|up
init|=
operator|new
name|UpdateOp
argument_list|(
name|id
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|up
operator|.
name|set
argument_list|(
literal|"_id"
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|up
operator|.
name|set
argument_list|(
name|NodeDocument
operator|.
name|DELETED_ONCE
argument_list|,
name|i
operator|%
literal|2
operator|==
literal|1
argument_list|)
expr_stmt|;
name|up
operator|.
name|set
argument_list|(
name|NodeDocument
operator|.
name|MODIFIED_IN_SECS
argument_list|,
name|now
operator|++
argument_list|)
expr_stmt|;
name|boolean
name|success
init|=
name|super
operator|.
name|ds
operator|.
name|create
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|up
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"document with "
operator|+
name|id
operator|+
literal|" not created"
argument_list|,
name|success
argument_list|)
expr_stmt|;
name|removeMe
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|QueryCondition
argument_list|>
name|conditions
init|=
operator|new
name|ArrayList
argument_list|<
name|QueryCondition
argument_list|>
argument_list|()
decl_stmt|;
comment|// matches every second
name|conditions
operator|.
name|add
argument_list|(
operator|new
name|QueryCondition
argument_list|(
name|NodeDocument
operator|.
name|DELETED_ONCE
argument_list|,
literal|"="
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// matches first eight
name|conditions
operator|.
name|add
argument_list|(
operator|new
name|QueryCondition
argument_list|(
name|NodeDocument
operator|.
name|MODIFIED_IN_SECS
argument_list|,
literal|"<"
argument_list|,
name|now
operator|-
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|NodeDocument
argument_list|>
name|result
init|=
name|rds
operator|.
name|query
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|base
argument_list|,
name|base
operator|+
literal|"A"
argument_list|,
name|RDBDocumentStore
operator|.
name|EMPTY_KEY_PATTERN
argument_list|,
name|conditions
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|result
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRDBQueryKeyPatterns
parameter_list|()
block|{
if|if
condition|(
name|ds
operator|instanceof
name|RDBDocumentStore
condition|)
block|{
name|RDBDocumentStore
name|rds
init|=
operator|(
name|RDBDocumentStore
operator|)
name|ds
decl_stmt|;
comment|// create ten documents
name|String
name|base
init|=
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|".testRDBQuery-"
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
comment|// every second is a "regular" path
name|String
name|id
init|=
literal|"1:"
operator|+
operator|(
name|i
operator|%
literal|2
operator|==
literal|1
condition|?
literal|"p"
else|:
literal|""
operator|)
operator|+
literal|"/"
operator|+
name|base
operator|+
name|i
decl_stmt|;
name|UpdateOp
name|up
init|=
operator|new
name|UpdateOp
argument_list|(
name|id
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|up
operator|.
name|set
argument_list|(
literal|"_id"
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|up
operator|.
name|set
argument_list|(
literal|"_test"
argument_list|,
name|base
argument_list|)
expr_stmt|;
name|boolean
name|success
init|=
name|super
operator|.
name|ds
operator|.
name|create
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|up
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"document with "
operator|+
name|id
operator|+
literal|" not created"
argument_list|,
name|success
argument_list|)
expr_stmt|;
name|removeMe
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|removeMe
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|QueryCondition
argument_list|>
name|conditions
init|=
operator|new
name|ArrayList
argument_list|<
name|QueryCondition
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|NodeDocument
argument_list|>
name|result
init|=
name|rds
operator|.
name|query
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|NodeDocument
operator|.
name|MIN_ID_VALUE
argument_list|,
name|NodeDocument
operator|.
name|MAX_ID_VALUE
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"_:/%"
argument_list|,
literal|"__:/%"
argument_list|,
literal|"___:/%"
argument_list|)
argument_list|,
name|conditions
argument_list|,
literal|10000
argument_list|)
decl_stmt|;
for|for
control|(
name|NodeDocument
name|d
range|:
name|result
control|)
block|{
if|if
condition|(
name|base
operator|.
name|equals
argument_list|(
name|d
operator|.
name|get
argument_list|(
literal|"_test"
argument_list|)
argument_list|)
condition|)
block|{
name|assertTrue
argument_list|(
name|d
operator|.
name|getId
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"1:p"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

