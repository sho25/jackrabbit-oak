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
name|file
operator|.
name|tar
operator|.
name|index
package|;
end_package

begin_comment
comment|/**  * An entry in the index of entries of a TAR file.  */
end_comment

begin_interface
specifier|public
interface|interface
name|IndexEntry
block|{
comment|/**      * Return the most significant bits of the identifier of this entry.      *      * @return the most significant bits of the identifier of this entry.      */
name|long
name|getMsb
parameter_list|()
function_decl|;
comment|/**      * Return the least significant bits of the identifier of this entry.      *      * @return the least significant bits of the identifier of this entry.      */
name|long
name|getLsb
parameter_list|()
function_decl|;
comment|/**      * Return the position of this entry in the TAR file.      *      * @return the position of this entry in the TAR file.      */
name|int
name|getPosition
parameter_list|()
function_decl|;
comment|/**      * Return the length of this entry in the TAR file.      *      * @return the length of this entry in the TAR file.      */
name|int
name|getLength
parameter_list|()
function_decl|;
comment|/**      * Return the full generation of this entry.      *      * @return the full generation of this entry.      */
name|int
name|getFullGeneration
parameter_list|()
function_decl|;
comment|/**      * Return the tail generation of this entry.      *      * @return the tail generation of this entry.      */
name|int
name|getTailGeneration
parameter_list|()
function_decl|;
comment|/**      * Return {@code true} if this entry was generated as part of a tail      * commit.      *      * @return {@code true} if this entry was generated as part of a tail      * commit.      */
name|boolean
name|isTail
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

