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
name|javax
operator|.
name|jcr
operator|.
name|Value
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
name|api
operator|.
name|security
operator|.
name|user
operator|.
name|Authorizable
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
name|api
operator|.
name|security
operator|.
name|user
operator|.
name|Group
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
name|api
operator|.
name|security
operator|.
name|user
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
name|jackrabbit
operator|.
name|api
operator|.
name|security
operator|.
name|user
operator|.
name|User
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
name|AuthorizableType
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
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
import|;
end_import

begin_class
class|class
name|XPathQueryBuilder
implements|implements
name|QueryBuilder
argument_list|<
name|Condition
argument_list|>
block|{
specifier|private
name|AuthorizableType
name|selectorType
init|=
name|AuthorizableType
operator|.
name|AUTHORIZABLE
decl_stmt|;
specifier|private
name|String
name|groupID
decl_stmt|;
specifier|private
name|boolean
name|declaredMembersOnly
decl_stmt|;
specifier|private
name|Condition
name|condition
decl_stmt|;
specifier|private
name|String
name|sortProperty
decl_stmt|;
specifier|private
name|Direction
name|sortDirection
init|=
name|Direction
operator|.
name|ASCENDING
decl_stmt|;
specifier|private
name|boolean
name|sortIgnoreCase
decl_stmt|;
specifier|private
name|Value
name|bound
decl_stmt|;
specifier|private
name|long
name|offset
decl_stmt|;
specifier|private
name|long
name|maxCount
init|=
name|Long
operator|.
name|MAX_VALUE
decl_stmt|;
comment|//-------------------------------------------------------< QueryBuilder>---
annotation|@
name|Override
specifier|public
name|void
name|setSelector
parameter_list|(
annotation|@
name|NotNull
name|Class
argument_list|<
name|?
extends|extends
name|Authorizable
argument_list|>
name|selector
parameter_list|)
block|{
if|if
condition|(
name|User
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|selector
argument_list|)
condition|)
block|{
name|selectorType
operator|=
name|AuthorizableType
operator|.
name|USER
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|Group
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|selector
argument_list|)
condition|)
block|{
name|selectorType
operator|=
name|AuthorizableType
operator|.
name|GROUP
expr_stmt|;
block|}
else|else
block|{
name|selectorType
operator|=
name|AuthorizableType
operator|.
name|AUTHORIZABLE
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|setScope
parameter_list|(
annotation|@
name|NotNull
name|String
name|groupID
parameter_list|,
name|boolean
name|declaredOnly
parameter_list|)
block|{
name|this
operator|.
name|groupID
operator|=
name|groupID
expr_stmt|;
name|declaredMembersOnly
operator|=
name|declaredOnly
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setCondition
parameter_list|(
annotation|@
name|NotNull
name|Condition
name|condition
parameter_list|)
block|{
name|this
operator|.
name|condition
operator|=
name|condition
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setSortOrder
parameter_list|(
annotation|@
name|NotNull
name|String
name|propertyName
parameter_list|,
annotation|@
name|NotNull
name|Direction
name|direction
parameter_list|,
name|boolean
name|ignoreCase
parameter_list|)
block|{
name|sortProperty
operator|=
name|propertyName
expr_stmt|;
name|sortDirection
operator|=
name|direction
expr_stmt|;
name|sortIgnoreCase
operator|=
name|ignoreCase
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setSortOrder
parameter_list|(
annotation|@
name|NotNull
name|String
name|propertyName
parameter_list|,
annotation|@
name|NotNull
name|Direction
name|direction
parameter_list|)
block|{
name|setSortOrder
argument_list|(
name|propertyName
argument_list|,
name|direction
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setLimit
parameter_list|(
annotation|@
name|Nullable
name|Value
name|bound
parameter_list|,
name|long
name|maxCount
parameter_list|)
block|{
comment|// reset the offset before setting bound value/maxCount
name|offset
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|bound
operator|=
name|bound
expr_stmt|;
name|setMaxCount
argument_list|(
name|maxCount
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setLimit
parameter_list|(
name|long
name|offset
parameter_list|,
name|long
name|maxCount
parameter_list|)
block|{
comment|// reset the bound value before setting offset/maxCount
name|bound
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
name|setMaxCount
argument_list|(
name|maxCount
argument_list|)
expr_stmt|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|Condition
name|nameMatches
parameter_list|(
annotation|@
name|NotNull
name|String
name|pattern
parameter_list|)
block|{
return|return
operator|new
name|Condition
operator|.
name|Node
argument_list|(
name|pattern
argument_list|)
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|Condition
name|neq
parameter_list|(
annotation|@
name|NotNull
name|String
name|relPath
parameter_list|,
annotation|@
name|NotNull
name|Value
name|value
parameter_list|)
block|{
return|return
operator|new
name|Condition
operator|.
name|Property
argument_list|(
name|relPath
argument_list|,
name|RelationOp
operator|.
name|NE
argument_list|,
name|value
argument_list|)
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|Condition
name|eq
parameter_list|(
annotation|@
name|NotNull
name|String
name|relPath
parameter_list|,
annotation|@
name|NotNull
name|Value
name|value
parameter_list|)
block|{
return|return
operator|new
name|Condition
operator|.
name|Property
argument_list|(
name|relPath
argument_list|,
name|RelationOp
operator|.
name|EQ
argument_list|,
name|value
argument_list|)
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|Condition
name|lt
parameter_list|(
annotation|@
name|NotNull
name|String
name|relPath
parameter_list|,
annotation|@
name|NotNull
name|Value
name|value
parameter_list|)
block|{
return|return
operator|new
name|Condition
operator|.
name|Property
argument_list|(
name|relPath
argument_list|,
name|RelationOp
operator|.
name|LT
argument_list|,
name|value
argument_list|)
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|Condition
name|le
parameter_list|(
annotation|@
name|NotNull
name|String
name|relPath
parameter_list|,
annotation|@
name|NotNull
name|Value
name|value
parameter_list|)
block|{
return|return
operator|new
name|Condition
operator|.
name|Property
argument_list|(
name|relPath
argument_list|,
name|RelationOp
operator|.
name|LE
argument_list|,
name|value
argument_list|)
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|Condition
name|gt
parameter_list|(
annotation|@
name|NotNull
name|String
name|relPath
parameter_list|,
annotation|@
name|NotNull
name|Value
name|value
parameter_list|)
block|{
return|return
operator|new
name|Condition
operator|.
name|Property
argument_list|(
name|relPath
argument_list|,
name|RelationOp
operator|.
name|GT
argument_list|,
name|value
argument_list|)
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|Condition
name|ge
parameter_list|(
annotation|@
name|NotNull
name|String
name|relPath
parameter_list|,
annotation|@
name|NotNull
name|Value
name|value
parameter_list|)
block|{
return|return
operator|new
name|Condition
operator|.
name|Property
argument_list|(
name|relPath
argument_list|,
name|RelationOp
operator|.
name|GE
argument_list|,
name|value
argument_list|)
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|Condition
name|exists
parameter_list|(
annotation|@
name|NotNull
name|String
name|relPath
parameter_list|)
block|{
return|return
operator|new
name|Condition
operator|.
name|Property
argument_list|(
name|relPath
argument_list|,
name|RelationOp
operator|.
name|EX
argument_list|)
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|Condition
name|like
parameter_list|(
annotation|@
name|NotNull
name|String
name|relPath
parameter_list|,
annotation|@
name|NotNull
name|String
name|pattern
parameter_list|)
block|{
return|return
operator|new
name|Condition
operator|.
name|Property
argument_list|(
name|relPath
argument_list|,
name|RelationOp
operator|.
name|LIKE
argument_list|,
name|pattern
argument_list|)
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|Condition
name|contains
parameter_list|(
annotation|@
name|NotNull
name|String
name|relPath
parameter_list|,
annotation|@
name|NotNull
name|String
name|searchExpr
parameter_list|)
block|{
return|return
operator|new
name|Condition
operator|.
name|Contains
argument_list|(
name|relPath
argument_list|,
name|searchExpr
argument_list|)
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|Condition
name|impersonates
parameter_list|(
annotation|@
name|NotNull
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|Condition
operator|.
name|Impersonation
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|Condition
name|not
parameter_list|(
annotation|@
name|NotNull
name|Condition
name|condition
parameter_list|)
block|{
return|return
operator|new
name|Condition
operator|.
name|Not
argument_list|(
name|condition
argument_list|)
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|Condition
name|and
parameter_list|(
annotation|@
name|NotNull
name|Condition
name|condition1
parameter_list|,
annotation|@
name|NotNull
name|Condition
name|condition2
parameter_list|)
block|{
return|return
operator|new
name|Condition
operator|.
name|And
argument_list|(
name|condition1
argument_list|,
name|condition2
argument_list|)
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|Condition
name|or
parameter_list|(
annotation|@
name|NotNull
name|Condition
name|condition1
parameter_list|,
annotation|@
name|NotNull
name|Condition
name|condition2
parameter_list|)
block|{
return|return
operator|new
name|Condition
operator|.
name|Or
argument_list|(
name|condition1
argument_list|,
name|condition2
argument_list|)
return|;
block|}
comment|//-----------------------------------------------------------< internal>---
name|Condition
name|property
parameter_list|(
name|String
name|relPath
parameter_list|,
name|RelationOp
name|op
parameter_list|,
name|Value
name|value
parameter_list|)
block|{
return|return
operator|new
name|Condition
operator|.
name|Property
argument_list|(
name|relPath
argument_list|,
name|op
argument_list|,
name|value
argument_list|)
return|;
block|}
name|AuthorizableType
name|getSelectorType
parameter_list|()
block|{
return|return
name|selectorType
return|;
block|}
name|String
name|getGroupID
parameter_list|()
block|{
return|return
name|groupID
return|;
block|}
name|boolean
name|isDeclaredMembersOnly
parameter_list|()
block|{
return|return
name|declaredMembersOnly
return|;
block|}
name|Condition
name|getCondition
parameter_list|()
block|{
return|return
name|condition
return|;
block|}
name|String
name|getSortProperty
parameter_list|()
block|{
return|return
name|sortProperty
return|;
block|}
name|Direction
name|getSortDirection
parameter_list|()
block|{
return|return
name|sortDirection
return|;
block|}
name|boolean
name|getSortIgnoreCase
parameter_list|()
block|{
return|return
name|sortIgnoreCase
return|;
block|}
name|Value
name|getBound
parameter_list|()
block|{
return|return
name|bound
return|;
block|}
name|long
name|getOffset
parameter_list|()
block|{
return|return
name|offset
return|;
block|}
name|long
name|getMaxCount
parameter_list|()
block|{
return|return
name|maxCount
return|;
block|}
specifier|private
name|void
name|setMaxCount
parameter_list|(
name|long
name|maxCount
parameter_list|)
block|{
if|if
condition|(
name|maxCount
operator|==
operator|-
literal|1
condition|)
block|{
name|this
operator|.
name|maxCount
operator|=
name|Long
operator|.
name|MAX_VALUE
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|maxCount
operator|=
name|maxCount
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

