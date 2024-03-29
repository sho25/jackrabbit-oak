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
name|ArrayList
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
name|RepositoryException
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

begin_interface
interface|interface
name|Condition
block|{
name|void
name|accept
parameter_list|(
name|ConditionVisitor
name|visitor
parameter_list|)
throws|throws
name|RepositoryException
function_decl|;
comment|//-----------------------------------------------------< Node Condition>---
class|class
name|Node
implements|implements
name|Condition
block|{
specifier|private
specifier|final
name|String
name|pattern
decl_stmt|;
specifier|public
name|Node
parameter_list|(
name|String
name|pattern
parameter_list|)
block|{
name|this
operator|.
name|pattern
operator|=
name|pattern
expr_stmt|;
block|}
specifier|public
name|String
name|getPattern
parameter_list|()
block|{
return|return
name|pattern
return|;
block|}
specifier|public
name|void
name|accept
parameter_list|(
name|ConditionVisitor
name|visitor
parameter_list|)
block|{
name|visitor
operator|.
name|visit
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
comment|//-------------------------------------------------< Property Condition>---
class|class
name|Property
implements|implements
name|Condition
block|{
specifier|private
specifier|final
name|String
name|relPath
decl_stmt|;
specifier|private
specifier|final
name|RelationOp
name|op
decl_stmt|;
specifier|private
specifier|final
name|Value
name|value
decl_stmt|;
specifier|private
specifier|final
name|String
name|pattern
decl_stmt|;
specifier|public
name|Property
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
name|this
operator|.
name|relPath
operator|=
name|relPath
expr_stmt|;
name|this
operator|.
name|op
operator|=
name|op
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
name|pattern
operator|=
literal|null
expr_stmt|;
block|}
specifier|public
name|Property
parameter_list|(
name|String
name|relPath
parameter_list|,
name|RelationOp
name|op
parameter_list|,
name|String
name|pattern
parameter_list|)
block|{
name|this
operator|.
name|relPath
operator|=
name|relPath
expr_stmt|;
name|this
operator|.
name|op
operator|=
name|op
expr_stmt|;
name|value
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|pattern
operator|=
name|pattern
expr_stmt|;
block|}
specifier|public
name|Property
parameter_list|(
name|String
name|relPath
parameter_list|,
name|RelationOp
name|op
parameter_list|)
block|{
name|this
operator|.
name|relPath
operator|=
name|relPath
expr_stmt|;
name|this
operator|.
name|op
operator|=
name|op
expr_stmt|;
name|value
operator|=
literal|null
expr_stmt|;
name|pattern
operator|=
literal|null
expr_stmt|;
block|}
specifier|public
name|String
name|getRelPath
parameter_list|()
block|{
return|return
name|relPath
return|;
block|}
specifier|public
name|RelationOp
name|getOp
parameter_list|()
block|{
return|return
name|op
return|;
block|}
specifier|public
name|Value
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
specifier|public
name|String
name|getPattern
parameter_list|()
block|{
return|return
name|pattern
return|;
block|}
specifier|public
name|void
name|accept
parameter_list|(
name|ConditionVisitor
name|visitor
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|visitor
operator|.
name|visit
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
comment|//-------------------------------------------------< Contains Condition>---
class|class
name|Contains
implements|implements
name|Condition
block|{
specifier|private
specifier|final
name|String
name|relPath
decl_stmt|;
specifier|private
specifier|final
name|String
name|searchExpr
decl_stmt|;
specifier|public
name|Contains
parameter_list|(
name|String
name|relPath
parameter_list|,
name|String
name|searchExpr
parameter_list|)
block|{
name|this
operator|.
name|relPath
operator|=
name|relPath
expr_stmt|;
name|this
operator|.
name|searchExpr
operator|=
name|searchExpr
expr_stmt|;
block|}
specifier|public
name|String
name|getRelPath
parameter_list|()
block|{
return|return
name|relPath
return|;
block|}
specifier|public
name|String
name|getSearchExpr
parameter_list|()
block|{
return|return
name|searchExpr
return|;
block|}
specifier|public
name|void
name|accept
parameter_list|(
name|ConditionVisitor
name|visitor
parameter_list|)
block|{
name|visitor
operator|.
name|visit
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
comment|//--------------------------------------------< Impersonation Condition>---
class|class
name|Impersonation
implements|implements
name|Condition
block|{
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|public
name|Impersonation
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
specifier|public
name|void
name|accept
parameter_list|(
name|ConditionVisitor
name|visitor
parameter_list|)
block|{
name|visitor
operator|.
name|visit
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
comment|//------------------------------------------------------< Not Condition>---
class|class
name|Not
implements|implements
name|Condition
block|{
specifier|private
specifier|final
name|Condition
name|condition
decl_stmt|;
specifier|public
name|Not
parameter_list|(
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
specifier|public
name|Condition
name|getCondition
parameter_list|()
block|{
return|return
name|condition
return|;
block|}
specifier|public
name|void
name|accept
parameter_list|(
name|ConditionVisitor
name|visitor
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|visitor
operator|.
name|visit
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
comment|//-------------------------------------------------< Compound Condition>---
specifier|abstract
class|class
name|Compound
implements|implements
name|Condition
implements|,
name|Iterable
argument_list|<
name|Condition
argument_list|>
block|{
specifier|private
specifier|final
name|List
argument_list|<
name|Condition
argument_list|>
name|conditions
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|Compound
parameter_list|(
name|Condition
name|condition1
parameter_list|,
name|Condition
name|condition2
parameter_list|)
block|{
name|conditions
operator|.
name|add
argument_list|(
name|condition1
argument_list|)
expr_stmt|;
name|conditions
operator|.
name|add
argument_list|(
name|condition2
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Iterator
argument_list|<
name|Condition
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|conditions
operator|.
name|iterator
argument_list|()
return|;
block|}
block|}
comment|//------------------------------------------------------< And Condition>---
class|class
name|And
extends|extends
name|Compound
block|{
specifier|public
name|And
parameter_list|(
name|Condition
name|condition1
parameter_list|,
name|Condition
name|condition2
parameter_list|)
block|{
name|super
argument_list|(
name|condition1
argument_list|,
name|condition2
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|accept
parameter_list|(
name|ConditionVisitor
name|visitor
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|visitor
operator|.
name|visit
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
comment|//-------------------------------------------------------< Or Condition>---
class|class
name|Or
extends|extends
name|Compound
block|{
specifier|public
name|Or
parameter_list|(
name|Condition
name|condition1
parameter_list|,
name|Condition
name|condition2
parameter_list|)
block|{
name|super
argument_list|(
name|condition1
argument_list|,
name|condition2
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|accept
parameter_list|(
name|ConditionVisitor
name|visitor
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|visitor
operator|.
name|visit
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_interface

end_unit

