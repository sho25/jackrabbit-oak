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
name|query
operator|.
name|ast
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
name|query
operator|.
name|index
operator|.
name|FilterImpl
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
name|Filter
import|;
end_import

begin_comment
comment|/**  * The "issamenode(...)" join condition.  */
end_comment

begin_class
specifier|public
class|class
name|SameNodeJoinConditionImpl
extends|extends
name|JoinConditionImpl
block|{
specifier|private
specifier|final
name|String
name|selector1Name
decl_stmt|;
specifier|private
specifier|final
name|String
name|selector2Name
decl_stmt|;
specifier|private
specifier|final
name|String
name|selector2Path
decl_stmt|;
specifier|private
name|SelectorImpl
name|selector1
decl_stmt|;
specifier|private
name|SelectorImpl
name|selector2
decl_stmt|;
specifier|public
name|SameNodeJoinConditionImpl
parameter_list|(
name|String
name|selector1Name
parameter_list|,
name|String
name|selector2Name
parameter_list|,
name|String
name|selector2Path
parameter_list|)
block|{
name|this
operator|.
name|selector1Name
operator|=
name|selector1Name
expr_stmt|;
name|this
operator|.
name|selector2Name
operator|=
name|selector2Name
expr_stmt|;
name|this
operator|.
name|selector2Path
operator|=
name|selector2Path
expr_stmt|;
block|}
specifier|public
name|String
name|getSelector1Name
parameter_list|()
block|{
return|return
name|selector1Name
return|;
block|}
specifier|public
name|String
name|getSelector2Name
parameter_list|()
block|{
return|return
name|selector2Name
return|;
block|}
specifier|public
name|String
name|getSelector2Path
parameter_list|()
block|{
return|return
name|selector2Path
return|;
block|}
annotation|@
name|Override
name|boolean
name|accept
parameter_list|(
name|AstVisitor
name|v
parameter_list|)
block|{
return|return
name|v
operator|.
name|visit
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"issamenode("
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|getSelector1Name
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|getSelector2Name
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|selector2Path
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|quotePath
argument_list|(
name|selector2Path
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|void
name|bindSelector
parameter_list|(
name|SourceImpl
name|source
parameter_list|)
block|{
name|selector1
operator|=
name|source
operator|.
name|getExistingSelector
argument_list|(
name|selector1Name
argument_list|)
expr_stmt|;
name|selector2
operator|=
name|source
operator|.
name|getExistingSelector
argument_list|(
name|selector2Name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|evaluate
parameter_list|()
block|{
name|String
name|p1
init|=
name|selector1
operator|.
name|currentPath
argument_list|()
decl_stmt|;
name|String
name|p2
init|=
name|selector2
operator|.
name|currentPath
argument_list|()
decl_stmt|;
return|return
name|p1
operator|.
name|equals
argument_list|(
name|p2
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|apply
parameter_list|(
name|FilterImpl
name|f
parameter_list|)
block|{
name|String
name|p1
init|=
name|selector1
operator|.
name|currentPath
argument_list|()
decl_stmt|;
name|String
name|p2
init|=
name|selector2
operator|.
name|currentPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|f
operator|.
name|getSelector
argument_list|()
operator|==
name|selector1
condition|)
block|{
name|f
operator|.
name|restrictPath
argument_list|(
name|p2
argument_list|,
name|Filter
operator|.
name|PathRestriction
operator|.
name|EXACT
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|f
operator|.
name|getSelector
argument_list|()
operator|==
name|selector2
condition|)
block|{
name|f
operator|.
name|restrictPath
argument_list|(
name|p1
argument_list|,
name|Filter
operator|.
name|PathRestriction
operator|.
name|EXACT
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

