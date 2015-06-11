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
name|remote
package|;
end_package

begin_comment
comment|/**  * Represents a set of filters that can be applied when a binary object is read  * from the repository.  */
end_comment

begin_class
specifier|public
class|class
name|RemoteBinaryFilters
block|{
comment|/**      * Return the starting offset into the binary object. This method returns      * {@code 0} by default, meaning that the binary object should be read from      * the beginning.      */
specifier|public
name|long
name|getStart
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
comment|/**      * Return the number of bytes to read. This method returns {@code -1} by      * default, meaning that the binary object should be read until the end.      */
specifier|public
name|long
name|getCount
parameter_list|()
block|{
return|return
operator|-
literal|1
return|;
block|}
block|}
end_class

end_unit

