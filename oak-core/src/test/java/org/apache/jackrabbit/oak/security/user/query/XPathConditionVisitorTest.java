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
name|security
operator|.
name|user
operator|.
name|query
package|;
end_package

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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Value
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
name|ImmutableMap
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
name|Iterators
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
name|AbstractSecurityTest
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
name|QueryUtils
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
name|namepath
operator|.
name|impl
operator|.
name|LocalNameMapper
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
name|namepath
operator|.
name|NamePathMapper
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
name|namepath
operator|.
name|impl
operator|.
name|NamePathMapperImpl
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
name|security
operator|.
name|user
operator|.
name|UserConstants
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

begin_class
specifier|public
class|class
name|XPathConditionVisitorTest
extends|extends
name|AbstractSecurityTest
block|{
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|LOCAL
init|=
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"rcj"
argument_list|,
literal|"http://www.jcp.org/jcr/1.0"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|REL_PATH
init|=
literal|"r'e/l/path"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|SERACH_EXPR
init|=
literal|"s%e\\%arch\\E[:]xpr"
decl_stmt|;
specifier|private
name|StringBuilder
name|statement
decl_stmt|;
specifier|private
name|XPathConditionVisitor
name|visitor
decl_stmt|;
specifier|private
name|Condition
operator|.
name|Contains
name|testCondition
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|before
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|before
argument_list|()
expr_stmt|;
name|statement
operator|=
operator|new
name|StringBuilder
argument_list|()
expr_stmt|;
name|visitor
operator|=
operator|new
name|XPathConditionVisitor
argument_list|(
name|statement
argument_list|,
name|getNamePathMapper
argument_list|()
argument_list|,
name|getUserManager
argument_list|(
name|root
argument_list|)
argument_list|)
expr_stmt|;
name|testCondition
operator|=
operator|new
name|Condition
operator|.
name|Contains
argument_list|(
name|REL_PATH
argument_list|,
name|SERACH_EXPR
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|NamePathMapper
name|getNamePathMapper
parameter_list|()
block|{
return|return
operator|new
name|NamePathMapperImpl
argument_list|(
operator|new
name|LocalNameMapper
argument_list|(
name|root
argument_list|,
name|LOCAL
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|void
name|reduceCompoundConditionToSingleTerm
parameter_list|(
annotation|@
name|NotNull
name|Condition
operator|.
name|Compound
name|condition
parameter_list|)
block|{
name|Iterator
argument_list|<
name|Condition
argument_list|>
name|it
init|=
name|condition
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|it
operator|.
name|next
argument_list|()
expr_stmt|;
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|Iterators
operator|.
name|size
argument_list|(
name|condition
operator|.
name|iterator
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testVisitNode
parameter_list|()
throws|throws
name|Exception
block|{
name|visitor
operator|.
name|visit
argument_list|(
operator|new
name|Condition
operator|.
name|Node
argument_list|(
name|SERACH_EXPR
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|s
init|=
name|statement
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|s
operator|.
name|contains
argument_list|(
name|SERACH_EXPR
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|s
operator|.
name|contains
argument_list|(
name|QueryUtils
operator|.
name|escapeForQuery
argument_list|(
name|SERACH_EXPR
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|s
operator|.
name|contains
argument_list|(
name|QueryUtils
operator|.
name|escapeForQuery
argument_list|(
name|QueryUtils
operator|.
name|escapeNodeName
argument_list|(
name|SERACH_EXPR
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testVisitProperty
parameter_list|()
throws|throws
name|Exception
block|{
name|Value
name|v
init|=
name|getValueFactory
argument_list|(
name|root
argument_list|)
operator|.
name|createValue
argument_list|(
name|SERACH_EXPR
argument_list|)
decl_stmt|;
for|for
control|(
name|RelationOp
name|op
range|:
name|RelationOp
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|op
operator|==
name|RelationOp
operator|.
name|EX
operator|||
name|op
operator|==
name|RelationOp
operator|.
name|LIKE
condition|)
block|{
continue|continue;
block|}
name|visitor
operator|.
name|visit
argument_list|(
operator|new
name|Condition
operator|.
name|Property
argument_list|(
name|REL_PATH
argument_list|,
name|op
argument_list|,
name|v
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|s
init|=
name|statement
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|expected
init|=
name|QueryUtils
operator|.
name|escapeForQuery
argument_list|(
name|REL_PATH
argument_list|)
operator|+
name|op
operator|.
name|getOp
argument_list|()
operator|+
name|QueryUtil
operator|.
name|format
argument_list|(
name|v
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|s
argument_list|)
expr_stmt|;
comment|// reset statement for next operation
name|statement
operator|.
name|delete
argument_list|(
literal|0
argument_list|,
name|statement
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testVisitPropertyExists
parameter_list|()
throws|throws
name|Exception
block|{
name|visitor
operator|.
name|visit
argument_list|(
operator|(
name|Condition
operator|.
name|Property
operator|)
operator|new
name|XPathQueryBuilder
argument_list|()
operator|.
name|exists
argument_list|(
name|REL_PATH
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|QueryUtils
operator|.
name|escapeForQuery
argument_list|(
name|REL_PATH
argument_list|)
argument_list|,
name|statement
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testVisitPropertyLike
parameter_list|()
throws|throws
name|Exception
block|{
name|visitor
operator|.
name|visit
argument_list|(
operator|(
name|Condition
operator|.
name|Property
operator|)
operator|new
name|XPathQueryBuilder
argument_list|()
operator|.
name|like
argument_list|(
name|REL_PATH
argument_list|,
name|SERACH_EXPR
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|s
init|=
name|statement
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|s
operator|.
name|startsWith
argument_list|(
literal|"jcr:like("
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|s
operator|.
name|endsWith
argument_list|(
literal|")"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|s
operator|.
name|contains
argument_list|(
name|REL_PATH
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|s
operator|.
name|contains
argument_list|(
name|QueryUtils
operator|.
name|escapeForQuery
argument_list|(
name|REL_PATH
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|s
operator|.
name|contains
argument_list|(
name|SERACH_EXPR
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|s
operator|.
name|contains
argument_list|(
name|QueryUtils
operator|.
name|escapeForQuery
argument_list|(
name|SERACH_EXPR
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testVisitContains
parameter_list|()
block|{
name|visitor
operator|.
name|visit
argument_list|(
name|testCondition
argument_list|)
expr_stmt|;
name|String
name|s
init|=
name|statement
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|s
operator|.
name|startsWith
argument_list|(
literal|"jcr:contains("
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|s
operator|.
name|endsWith
argument_list|(
literal|")"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|s
operator|.
name|contains
argument_list|(
name|REL_PATH
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|s
operator|.
name|contains
argument_list|(
name|QueryUtils
operator|.
name|escapeForQuery
argument_list|(
name|REL_PATH
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|s
operator|.
name|contains
argument_list|(
name|SERACH_EXPR
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|s
operator|.
name|contains
argument_list|(
name|QueryUtils
operator|.
name|escapeForQuery
argument_list|(
name|SERACH_EXPR
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testVisitImpersonation
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|principalName
init|=
name|getTestUser
argument_list|()
operator|.
name|getPrincipal
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
empty_stmt|;
name|Condition
operator|.
name|Impersonation
name|c
init|=
operator|new
name|Condition
operator|.
name|Impersonation
argument_list|(
name|principalName
argument_list|)
decl_stmt|;
name|visitor
operator|.
name|visit
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|String
name|s
init|=
name|statement
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|s
operator|.
name|contains
argument_list|(
name|UserConstants
operator|.
name|REP_IMPERSONATORS
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|s
operator|.
name|contains
argument_list|(
literal|"@rcj:primaryType='"
operator|+
name|UserConstants
operator|.
name|NT_REP_USER
operator|+
literal|"'"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testVisitImpersonationAdmin
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|adminPrincipalName
init|=
name|getUserManager
argument_list|(
name|root
argument_list|)
operator|.
name|getAuthorizable
argument_list|(
name|getUserConfiguration
argument_list|()
operator|.
name|getParameters
argument_list|()
operator|.
name|getConfigValue
argument_list|(
name|UserConstants
operator|.
name|PARAM_ADMIN_ID
argument_list|,
name|UserConstants
operator|.
name|DEFAULT_ADMIN_ID
argument_list|)
argument_list|)
operator|.
name|getPrincipal
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
name|Condition
operator|.
name|Impersonation
name|c
init|=
operator|new
name|Condition
operator|.
name|Impersonation
argument_list|(
name|adminPrincipalName
argument_list|)
decl_stmt|;
name|visitor
operator|.
name|visit
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|String
name|s
init|=
name|statement
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|s
operator|.
name|contains
argument_list|(
name|UserConstants
operator|.
name|REP_IMPERSONATORS
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|s
operator|.
name|contains
argument_list|(
literal|"@rcj:primaryType='"
operator|+
name|UserConstants
operator|.
name|NT_REP_USER
operator|+
literal|"'"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testVisitNot
parameter_list|()
throws|throws
name|Exception
block|{
name|visitor
operator|.
name|visit
argument_list|(
operator|new
name|Condition
operator|.
name|Not
argument_list|(
name|testCondition
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|statement
operator|.
name|toString
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"not("
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|statement
operator|.
name|toString
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|")"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testVisitAnd
parameter_list|()
throws|throws
name|Exception
block|{
name|visitor
operator|.
name|visit
argument_list|(
operator|new
name|Condition
operator|.
name|And
argument_list|(
name|testCondition
argument_list|,
name|testCondition
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|statement
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|" and "
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testVisitAndSingle
parameter_list|()
throws|throws
name|Exception
block|{
name|Condition
operator|.
name|And
name|c
init|=
operator|new
name|Condition
operator|.
name|And
argument_list|(
name|testCondition
argument_list|,
name|testCondition
argument_list|)
decl_stmt|;
name|reduceCompoundConditionToSingleTerm
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|visitor
operator|.
name|visit
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|statement
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|" and "
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testVisitOr
parameter_list|()
throws|throws
name|Exception
block|{
name|visitor
operator|.
name|visit
argument_list|(
operator|new
name|Condition
operator|.
name|Or
argument_list|(
name|testCondition
argument_list|,
name|testCondition
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|s
init|=
name|statement
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|s
operator|.
name|contains
argument_list|(
literal|" or "
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|s
operator|.
name|startsWith
argument_list|(
literal|"("
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|s
operator|.
name|endsWith
argument_list|(
literal|"))"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testVisitOrSingle
parameter_list|()
throws|throws
name|Exception
block|{
name|Condition
operator|.
name|Or
name|c
init|=
operator|new
name|Condition
operator|.
name|Or
argument_list|(
name|testCondition
argument_list|,
name|testCondition
argument_list|)
decl_stmt|;
name|reduceCompoundConditionToSingleTerm
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|visitor
operator|.
name|visit
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|String
name|s
init|=
name|statement
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|s
operator|.
name|contains
argument_list|(
literal|" or "
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|s
operator|.
name|startsWith
argument_list|(
literal|"("
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|s
operator|.
name|endsWith
argument_list|(
literal|"))"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

