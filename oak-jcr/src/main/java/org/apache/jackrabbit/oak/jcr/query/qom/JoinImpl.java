begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law  * or agreed to in writing, software distributed under the License is  * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied. See the License for the specific language  * governing permissions and limitations under the License.  */
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
name|jcr
operator|.
name|query
operator|.
name|qom
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|query
operator|.
name|qom
operator|.
name|Join
import|;
end_import

begin_comment
comment|/**  * The implementation of the corresponding JCR interface.  */
end_comment

begin_class
specifier|public
class|class
name|JoinImpl
extends|extends
name|SourceImpl
implements|implements
name|Join
block|{
specifier|private
specifier|final
name|JoinConditionImpl
name|joinCondition
decl_stmt|;
specifier|private
specifier|final
name|JoinType
name|joinType
decl_stmt|;
specifier|private
specifier|final
name|SourceImpl
name|left
decl_stmt|;
specifier|private
specifier|final
name|SourceImpl
name|right
decl_stmt|;
specifier|public
name|JoinImpl
parameter_list|(
name|SourceImpl
name|left
parameter_list|,
name|SourceImpl
name|right
parameter_list|,
name|JoinType
name|joinType
parameter_list|,
name|JoinConditionImpl
name|joinCondition
parameter_list|)
block|{
name|this
operator|.
name|left
operator|=
name|left
expr_stmt|;
name|this
operator|.
name|right
operator|=
name|right
expr_stmt|;
name|this
operator|.
name|joinType
operator|=
name|joinType
expr_stmt|;
name|this
operator|.
name|joinCondition
operator|=
name|joinCondition
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|JoinConditionImpl
name|getJoinCondition
parameter_list|()
block|{
return|return
name|joinCondition
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getJoinType
parameter_list|()
block|{
return|return
name|joinType
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|SourceImpl
name|getLeft
parameter_list|()
block|{
return|return
name|left
return|;
block|}
annotation|@
name|Override
specifier|public
name|SourceImpl
name|getRight
parameter_list|()
block|{
return|return
name|right
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
name|joinType
operator|.
name|formatSql
argument_list|(
name|left
argument_list|,
name|right
argument_list|,
name|joinCondition
argument_list|)
return|;
block|}
block|}
end_class

end_unit

