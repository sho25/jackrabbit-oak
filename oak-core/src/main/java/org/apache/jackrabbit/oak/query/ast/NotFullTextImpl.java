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
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
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
name|query
operator|.
name|fulltext
operator|.
name|FullTextExpression
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
name|query
operator|.
name|index
operator|.
name|FilterImpl
import|;
end_import

begin_comment
comment|/**  * class used to "wrap" a {@code NOT CONTAINS} clause. The main differences with a {@link NotImpl}  * reside in the {@link NotImpl#evaluate()} and restricts.  */
end_comment

begin_class
specifier|public
class|class
name|NotFullTextImpl
extends|extends
name|NotImpl
block|{
specifier|public
name|NotFullTextImpl
parameter_list|(
annotation|@
name|Nonnull
name|FullTextSearchImpl
name|constraint
parameter_list|)
block|{
name|super
argument_list|(
operator|new
name|NotFullTextSearchImpl
argument_list|(
name|checkNotNull
argument_list|(
name|constraint
argument_list|)
argument_list|)
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
return|return
name|getConstraint
argument_list|()
operator|.
name|evaluate
argument_list|()
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
operator|!
name|f
operator|.
name|getSelector
argument_list|()
operator|.
name|isOuterJoinRightHandSide
argument_list|()
condition|)
block|{
name|getConstraint
argument_list|()
operator|.
name|restrict
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
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
name|getConstraint
argument_list|()
operator|.
name|restrictPushDown
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|FullTextExpression
name|getFullTextConstraint
parameter_list|(
name|SelectorImpl
name|s
parameter_list|)
block|{
return|return
name|getConstraint
argument_list|()
operator|.
name|getFullTextConstraint
argument_list|(
name|s
argument_list|)
return|;
block|}
block|}
end_class

end_unit

