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
name|observation
operator|.
name|filter
package|;
end_package

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
name|api
operator|.
name|Type
operator|.
name|STRING
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
name|memory
operator|.
name|EmptyNodeState
operator|.
name|EMPTY_NODE
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
name|assertFalse
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
name|function
operator|.
name|Predicate
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
name|junit
operator|.
name|Test
import|;
end_import

begin_class
specifier|public
class|class
name|PropertyPredicateTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|propertyMatch
parameter_list|()
block|{
name|String
name|name
init|=
literal|"foo"
decl_stmt|;
specifier|final
name|String
name|value
init|=
literal|"bar"
decl_stmt|;
name|PropertyPredicate
name|p
init|=
operator|new
name|PropertyPredicate
argument_list|(
name|name
argument_list|,
operator|new
name|Predicate
argument_list|<
name|PropertyState
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|test
parameter_list|(
name|PropertyState
name|property
parameter_list|)
block|{
return|return
name|value
operator|.
name|equals
argument_list|(
name|property
operator|.
name|getValue
argument_list|(
name|STRING
argument_list|)
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|p
operator|.
name|test
argument_list|(
name|createWithProperty
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|propertyMiss
parameter_list|()
block|{
name|String
name|name
init|=
literal|"foo"
decl_stmt|;
specifier|final
name|String
name|value
init|=
literal|"bar"
decl_stmt|;
name|PropertyPredicate
name|p
init|=
operator|new
name|PropertyPredicate
argument_list|(
name|name
argument_list|,
operator|new
name|Predicate
argument_list|<
name|PropertyState
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|test
parameter_list|(
name|PropertyState
name|property
parameter_list|)
block|{
return|return
literal|"baz"
operator|.
name|equals
argument_list|(
name|property
operator|.
name|getValue
argument_list|(
name|STRING
argument_list|)
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|p
operator|.
name|test
argument_list|(
name|createWithProperty
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|nonExistingProperty
parameter_list|()
block|{
name|String
name|name
init|=
literal|"foo"
decl_stmt|;
specifier|final
name|String
name|value
init|=
literal|"bar"
decl_stmt|;
name|PropertyPredicate
name|p
init|=
operator|new
name|PropertyPredicate
argument_list|(
literal|"any"
argument_list|,
operator|new
name|Predicate
argument_list|<
name|PropertyState
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|test
parameter_list|(
name|PropertyState
name|property
parameter_list|)
block|{
return|return
name|value
operator|.
name|equals
argument_list|(
name|property
operator|.
name|getValue
argument_list|(
name|STRING
argument_list|)
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|p
operator|.
name|test
argument_list|(
name|createWithProperty
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|NodeState
name|createWithProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
return|return
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
operator|.
name|getNodeState
argument_list|()
return|;
block|}
block|}
end_class

end_unit

