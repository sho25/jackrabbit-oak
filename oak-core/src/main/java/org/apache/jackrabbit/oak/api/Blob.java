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
name|api
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
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

begin_comment
comment|/**  * Immutable representation of a binary value of finite length.  */
end_comment

begin_interface
specifier|public
interface|interface
name|Blob
extends|extends
name|Comparable
argument_list|<
name|Blob
argument_list|>
block|{
comment|/**      * Returns a new stream for this value object. Multiple calls to this      * methods return equal instances: {@code getNewStream().equals(getNewStream())}.      * @return a new stream for this value based on an internal conversion.      */
annotation|@
name|Nonnull
name|InputStream
name|getNewStream
parameter_list|()
function_decl|;
comment|/**      * Returns the length of this blob.      *      * @return the length of this blob.      */
name|long
name|length
parameter_list|()
function_decl|;
comment|/**      * The SHA-256 digest of the underlying stream      * @return      */
name|byte
index|[]
name|sha256
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

