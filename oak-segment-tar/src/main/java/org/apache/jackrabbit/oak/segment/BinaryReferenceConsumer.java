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
package|;
end_package

begin_comment
comment|/**  * A consumer for references to external binaries. An implementor of this  * interface is called every time an external binary reference is written in the  * store.  */
end_comment

begin_interface
specifier|public
interface|interface
name|BinaryReferenceConsumer
block|{
comment|/**      * Consume the reference to an external binary.      *      * @param generation      The generation of the record referencing the      *                        binary.      * @param binaryReference The opaque string representation of the binary      *                        reference.      */
name|void
name|consume
parameter_list|(
name|int
name|generation
parameter_list|,
name|String
name|binaryReference
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

