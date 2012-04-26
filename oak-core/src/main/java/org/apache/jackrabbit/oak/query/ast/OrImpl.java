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

begin_class
specifier|public
class|class
name|OrImpl
extends|extends
name|ConstraintImpl
block|{
specifier|private
specifier|final
name|ConstraintImpl
name|constraint1
decl_stmt|;
specifier|private
specifier|final
name|ConstraintImpl
name|constraint2
decl_stmt|;
specifier|public
name|OrImpl
parameter_list|(
name|ConstraintImpl
name|constraint1
parameter_list|,
name|ConstraintImpl
name|constraint2
parameter_list|)
block|{
name|this
operator|.
name|constraint1
operator|=
name|constraint1
expr_stmt|;
name|this
operator|.
name|constraint2
operator|=
name|constraint2
expr_stmt|;
block|}
specifier|public
name|ConstraintImpl
name|getConstraint1
parameter_list|()
block|{
return|return
name|constraint1
return|;
block|}
specifier|public
name|ConstraintImpl
name|getConstraint2
parameter_list|()
block|{
return|return
name|constraint2
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
name|constraint1
operator|.
name|evaluate
argument_list|()
operator|||
name|constraint2
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
name|protect
argument_list|(
name|constraint1
argument_list|)
operator|+
literal|" OR "
operator|+
name|protect
argument_list|(
name|constraint2
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
comment|// ignore
comment|// TODO convert OR conditions to UNION
block|}
block|}
end_class

end_unit

