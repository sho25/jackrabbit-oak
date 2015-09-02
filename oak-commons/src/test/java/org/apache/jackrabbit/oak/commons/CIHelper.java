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
name|commons
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Objects
operator|.
name|equal
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|Boolean
operator|.
name|parseBoolean
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|System
operator|.
name|getenv
import|;
end_import

begin_comment
comment|/**  * Utility class for ITs to determine the environment running in.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|CIHelper
block|{
specifier|private
name|CIHelper
parameter_list|()
block|{ }
comment|/**      * @return  {@code true} iff running on      * https://travis-ci.org/      */
specifier|public
specifier|static
name|boolean
name|travis
parameter_list|()
block|{
return|return
name|parseBoolean
argument_list|(
name|getenv
argument_list|(
literal|"TRAVIS"
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * @return  {@code true} iff running on with {@code PROFILE=pedantic}      * @deprecated Travis builds do not use PROFILE anymore. Use {@link #travis()} instead.      */
specifier|public
specifier|static
name|boolean
name|travisPedantic
parameter_list|()
block|{
return|return
name|equal
argument_list|(
name|getenv
argument_list|(
literal|"PROFILE"
argument_list|)
argument_list|,
literal|"pedantic"
argument_list|)
return|;
block|}
comment|/**      * @return  {@code true} iff running on with {@code PROFILE=unittesting}      * @deprecated Travis builds do not use PROFILE anymore. Use {@link #travis()} instead.      */
specifier|public
specifier|static
name|boolean
name|travisUnitTesting
parameter_list|()
block|{
return|return
name|equal
argument_list|(
name|getenv
argument_list|(
literal|"PROFILE"
argument_list|)
argument_list|,
literal|"unittesting"
argument_list|)
return|;
block|}
comment|/**      * @return  {@code true} iff running on with {@code PROFILE=integrationTesting}      * @deprecated Travis builds do not use PROFILE anymore. Use {@link #travis()} instead.      */
specifier|public
specifier|static
name|boolean
name|travisIntegrationTesting
parameter_list|()
block|{
return|return
name|equal
argument_list|(
name|getenv
argument_list|(
literal|"PROFILE"
argument_list|)
argument_list|,
literal|"integrationTesting"
argument_list|)
return|;
block|}
block|}
end_class

end_unit

