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
operator|.
name|conversion
package|;
end_package

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|annotation
operator|.
name|versioning
operator|.
name|ProviderType
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

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_comment
comment|/**  *  * Provides a URI in exchange for a Value.  * Typically the Value will represent something where a URI is valuable and useful.  * Implementations of this interface must ensure that the Oak security model is delegated  * securely and not circumvented. Only Oak bundles should implement this provider as in most cases  * internal implementation details of Oak will be required to achieve the implementation. Ideally  * implementations should be carefully reviewed by peers.  */
end_comment

begin_interface
annotation|@
name|ProviderType
specifier|public
interface|interface
name|URIProvider
block|{
name|URI
name|toURI
parameter_list|(
name|Value
name|value
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

