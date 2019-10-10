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
name|authorization
operator|.
name|accesscontrol
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|security
operator|.
name|NamedAccessControlPolicy
import|;
end_import

begin_class
specifier|public
specifier|final
class|class
name|ReadPolicy
implements|implements
name|NamedAccessControlPolicy
block|{
specifier|public
specifier|static
specifier|final
name|NamedAccessControlPolicy
name|INSTANCE
init|=
operator|new
name|ReadPolicy
argument_list|()
decl_stmt|;
specifier|private
name|ReadPolicy
parameter_list|()
block|{     }
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"Grants read access on configured trees."
return|;
block|}
block|}
end_class

end_unit

