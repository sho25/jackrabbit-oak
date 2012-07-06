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

begin_comment
comment|/**  * Enumeration of the join types.  */
end_comment

begin_enum
specifier|public
enum|enum
name|JoinType
block|{
name|INNER
argument_list|(
literal|"inner join"
argument_list|)
block|,
name|LEFT_OUTER
argument_list|(
literal|"left outer join"
argument_list|)
block|,
name|RIGHT_OUTER
argument_list|(
literal|"right outer join"
argument_list|)
block|;
comment|/**      * The name of this join type.      */
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
name|JoinType
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
comment|/**      * Returns the join type.      */
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|name
return|;
block|}
block|}
end_enum

end_unit

