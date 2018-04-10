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
name|segment
operator|.
name|util
package|;
end_package

begin_class
specifier|public
specifier|final
class|class
name|RoleUtils
block|{
specifier|private
name|RoleUtils
parameter_list|()
block|{     }
specifier|public
specifier|static
name|String
name|maybeAppendRole
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|role
parameter_list|)
block|{
if|if
condition|(
name|role
operator|!=
literal|null
condition|)
block|{
return|return
name|name
operator|+
literal|" - "
operator|+
name|role
return|;
block|}
return|return
name|name
return|;
block|}
block|}
end_class

end_unit

