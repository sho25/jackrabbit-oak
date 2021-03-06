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
name|query
operator|.
name|ast
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
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
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
name|collect
operator|.
name|ImmutableSet
operator|.
name|of
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|emptySet
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|is
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
name|assertThat
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
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
name|AndImplTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|simplifyForUnion
parameter_list|()
block|{
name|ConstraintImpl
name|and
decl_stmt|,
name|op1
decl_stmt|,
name|op2
decl_stmt|,
name|op3
decl_stmt|,
name|op4
decl_stmt|;
name|Set
argument_list|<
name|ConstraintImpl
argument_list|>
name|expected
decl_stmt|;
name|op1
operator|=
name|mock
argument_list|(
name|ComparisonImpl
operator|.
name|class
argument_list|)
expr_stmt|;
name|op2
operator|=
name|mock
argument_list|(
name|ComparisonImpl
operator|.
name|class
argument_list|)
expr_stmt|;
name|and
operator|=
operator|new
name|AndImpl
argument_list|(
name|op1
argument_list|,
name|op2
argument_list|)
expr_stmt|;
name|expected
operator|=
name|emptySet
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|and
operator|.
name|convertToUnion
argument_list|()
argument_list|,
name|is
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
name|op1
operator|=
name|mockConstraint
argument_list|(
literal|"op1"
argument_list|,
name|ComparisonImpl
operator|.
name|class
argument_list|)
expr_stmt|;
name|op2
operator|=
name|mockConstraint
argument_list|(
literal|"op2"
argument_list|,
name|ComparisonImpl
operator|.
name|class
argument_list|)
expr_stmt|;
name|op3
operator|=
name|mockConstraint
argument_list|(
literal|"op3"
argument_list|,
name|ComparisonImpl
operator|.
name|class
argument_list|)
expr_stmt|;
name|and
operator|=
operator|new
name|AndImpl
argument_list|(
operator|new
name|OrImpl
argument_list|(
name|op1
argument_list|,
name|op2
argument_list|)
argument_list|,
name|op3
argument_list|)
expr_stmt|;
name|expected
operator|=
name|of
argument_list|(
operator|(
name|ConstraintImpl
operator|)
operator|new
name|AndImpl
argument_list|(
name|op1
argument_list|,
name|op3
argument_list|)
argument_list|,
operator|(
name|ConstraintImpl
operator|)
operator|new
name|AndImpl
argument_list|(
name|op2
argument_list|,
name|op3
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|and
operator|.
name|convertToUnion
argument_list|()
argument_list|,
name|is
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
name|op1
operator|=
name|mockConstraint
argument_list|(
literal|"op1"
argument_list|,
name|ComparisonImpl
operator|.
name|class
argument_list|)
expr_stmt|;
name|op2
operator|=
name|mockConstraint
argument_list|(
literal|"op2"
argument_list|,
name|ComparisonImpl
operator|.
name|class
argument_list|)
expr_stmt|;
name|op3
operator|=
name|mockConstraint
argument_list|(
literal|"op3"
argument_list|,
name|ComparisonImpl
operator|.
name|class
argument_list|)
expr_stmt|;
name|op4
operator|=
name|mockConstraint
argument_list|(
literal|"op4"
argument_list|,
name|ComparisonImpl
operator|.
name|class
argument_list|)
expr_stmt|;
name|and
operator|=
operator|new
name|AndImpl
argument_list|(
operator|new
name|OrImpl
argument_list|(
operator|new
name|OrImpl
argument_list|(
name|op1
argument_list|,
name|op4
argument_list|)
argument_list|,
name|op2
argument_list|)
argument_list|,
name|op3
argument_list|)
expr_stmt|;
name|expected
operator|=
name|of
argument_list|(
operator|(
name|ConstraintImpl
operator|)
operator|new
name|AndImpl
argument_list|(
name|op1
argument_list|,
name|op3
argument_list|)
argument_list|,
operator|(
name|ConstraintImpl
operator|)
operator|new
name|AndImpl
argument_list|(
name|op2
argument_list|,
name|op3
argument_list|)
argument_list|,
operator|(
name|ConstraintImpl
operator|)
operator|new
name|AndImpl
argument_list|(
name|op4
argument_list|,
name|op3
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|and
operator|.
name|convertToUnion
argument_list|()
argument_list|,
name|is
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * convenience method for having better assertion messages       *       * @param toString the {@link String#toString()} message to be shown. Cannot be null;      * @param clazz the class you want Mockito to generate for you.      * @return a Mockito instance of the provided ConstraintImpl      */
specifier|private
specifier|static
name|ConstraintImpl
name|mockConstraint
parameter_list|(
annotation|@
name|NotNull
name|String
name|toString
parameter_list|,
annotation|@
name|NotNull
name|Class
argument_list|<
name|?
extends|extends
name|ConstraintImpl
argument_list|>
name|clazz
parameter_list|)
block|{
name|ConstraintImpl
name|c
init|=
name|mock
argument_list|(
name|checkNotNull
argument_list|(
name|clazz
argument_list|)
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|c
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|checkNotNull
argument_list|(
name|toString
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|c
return|;
block|}
block|}
end_class

end_unit

