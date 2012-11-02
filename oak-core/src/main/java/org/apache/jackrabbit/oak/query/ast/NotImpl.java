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

begin_comment
comment|/**  * A "not" condition.  */
end_comment

begin_class
specifier|public
class|class
name|NotImpl
extends|extends
name|ConstraintImpl
block|{
specifier|private
specifier|final
name|ConstraintImpl
name|constraint
decl_stmt|;
specifier|public
name|NotImpl
parameter_list|(
name|ConstraintImpl
name|constraint
parameter_list|)
block|{
name|this
operator|.
name|constraint
operator|=
name|constraint
expr_stmt|;
block|}
specifier|public
name|ConstraintImpl
name|getConstraint
parameter_list|()
block|{
return|return
name|constraint
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|evaluate
parameter_list|()
block|{
return|return
operator|!
name|constraint
operator|.
name|evaluate
argument_list|()
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
return|return
literal|"not "
operator|+
name|protect
argument_list|(
name|constraint
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|restrict
parameter_list|(
name|FilterImpl
name|f
parameter_list|)
block|{
if|if
condition|(
name|f
operator|.
name|getSelector
argument_list|()
operator|.
name|outerJoin
condition|)
block|{
comment|// we need to be careful with the condition
comment|// "NOT (property IS NOT NULL)"
comment|// (which is the same as
comment|// "property IS NULL") because
comment|// this might cause an index to ignore
comment|// the join condition "property = x"
comment|// for example in:
comment|// "select * from a left outer join b on a.x = b.y
comment|// where not b.y is not null"
comment|// must not result in the index to check for
comment|// "b.y is null", because that would alter the
comment|// result
return|return;
block|}
comment|// ignore
comment|// TODO convert NOT conditions
block|}
annotation|@
name|Override
specifier|public
name|void
name|restrictPushDown
parameter_list|(
name|SelectorImpl
name|s
parameter_list|)
block|{
comment|// ignore
comment|// TODO convert NOT conditions
block|}
block|}
end_class

end_unit

