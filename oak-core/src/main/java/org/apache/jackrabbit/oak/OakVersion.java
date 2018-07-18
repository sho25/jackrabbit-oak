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

begin_comment
comment|/**  * Provides version information about Oak.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|OakVersion
block|{
specifier|private
specifier|static
name|String
name|version
init|=
literal|null
decl_stmt|;
specifier|private
name|OakVersion
parameter_list|()
block|{     }
comment|/**      * Returns the version of oak-core.      *       * @return the version (or "SNAPSHOT" when unknown)      */
annotation|@
name|NotNull
specifier|public
specifier|synchronized
specifier|static
name|String
name|getVersion
parameter_list|()
block|{
if|if
condition|(
name|version
operator|==
literal|null
condition|)
block|{
name|version
operator|=
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|commons
operator|.
name|OakVersion
operator|.
name|getVersion
argument_list|(
literal|"oak-core"
argument_list|,
name|OakVersion
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
return|return
name|version
return|;
block|}
block|}
end_class

end_unit

