begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|spi
operator|.
name|state
package|;
end_package

begin_comment
comment|/**  * Utility method for code that deals with node states.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|NodeStateUtils
block|{
specifier|private
name|NodeStateUtils
parameter_list|()
block|{     }
comment|/**      * Check whether the node or property with the given name is hidden, that      * is, if the node name starts with a ":".      *      * @param name the node or property name      * @return true if the item is hidden      */
specifier|public
specifier|static
name|boolean
name|isHidden
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|name
operator|.
name|startsWith
argument_list|(
literal|":"
argument_list|)
return|;
block|}
block|}
end_class

end_unit

