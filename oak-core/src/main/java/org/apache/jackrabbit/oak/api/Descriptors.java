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
name|api
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|CheckForNull
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
name|javax
operator|.
name|jcr
operator|.
name|Value
import|;
end_import

begin_comment
comment|/**  * Repository descriptors interface that is used to support providing the repository descriptors of  * {@link javax.jcr.Repository}  */
end_comment

begin_interface
specifier|public
interface|interface
name|Descriptors
block|{
comment|/**      * Returns a string array holding all descriptor keys available for this      * implementation, both the standard descriptors defined by the string      * constants in this interface and any implementation-specific descriptors.      * Used in conjunction with {@link #getValue(String key)} and      * {@link #getValues(String key)} to query information about this      * repository implementation.      *      * @return a string array holding all descriptor keys.      */
annotation|@
name|Nonnull
name|String
index|[]
name|getKeys
parameter_list|()
function_decl|;
comment|/**      * Returns {@code true} if {@code key} is a standard descriptor      * defined by the string constants in this interface and {@code false}      * if it is either a valid implementation-specific key or not a valid key.      *      * @param key a descriptor key.      * @return whether<code>key</code> is a standard descriptor.      */
name|boolean
name|isStandardDescriptor
parameter_list|(
annotation|@
name|Nonnull
name|String
name|key
parameter_list|)
function_decl|;
comment|/**      * Returns {@code true} if {@code key} is a valid single-value      * descriptor; otherwise returns {@code false}      *      * @param key a descriptor key.      * @return whether the specified descriptor is multi-valued.      * @since JCR 2.0      */
name|boolean
name|isSingleValueDescriptor
parameter_list|(
annotation|@
name|Nonnull
name|String
name|key
parameter_list|)
function_decl|;
comment|/**      * The value of a single-value descriptor is found by passing the key for      * that descriptor to this method. If {@code key} is the key of a      * multi-value descriptor or not a valid key this method returns      * {@code null}.      *      * @param key a descriptor key.      * @return The value of the indicated descriptor      */
annotation|@
name|CheckForNull
name|Value
name|getValue
parameter_list|(
annotation|@
name|Nonnull
name|String
name|key
parameter_list|)
function_decl|;
comment|/**      * The value array of a multi-value descriptor is found by passing the key      * for that descriptor to this method. If {@code key} is the key of a      * single-value descriptor then this method returns that value as an array      * of size one. If {@code key} is not a valid key this method returns      * {@code null}.      *      * @param key a descriptor key.      * @return the value array for the indicated descriptor      */
annotation|@
name|CheckForNull
name|Value
index|[]
name|getValues
parameter_list|(
annotation|@
name|Nonnull
name|String
name|key
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

