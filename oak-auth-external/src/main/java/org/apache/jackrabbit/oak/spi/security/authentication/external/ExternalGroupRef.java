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
name|spi
operator|.
name|security
operator|.
name|authentication
operator|.
name|external
package|;
end_package

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
import|;
end_import

begin_class
annotation|@
name|Deprecated
specifier|public
class|class
name|ExternalGroupRef
extends|extends
name|ExternalIdentityRef
block|{
comment|/**      * Creates a new external group ref with the given id and provider name      *      * @param id the id of the identity.      * @param providerName the name of the identity provider      */
specifier|public
name|ExternalGroupRef
parameter_list|(
annotation|@
name|NotNull
name|String
name|id
parameter_list|,
annotation|@
name|Nullable
name|String
name|providerName
parameter_list|)
block|{
name|super
argument_list|(
name|id
argument_list|,
name|providerName
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

