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
name|principal
package|;
end_package

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|Principal
import|;
end_import

begin_comment
comment|/**  * Principal to mark an system internal subject.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|SystemPrincipal
implements|implements
name|Principal
block|{
specifier|public
specifier|static
specifier|final
name|SystemPrincipal
name|INSTANCE
init|=
operator|new
name|SystemPrincipal
argument_list|()
decl_stmt|;
specifier|private
name|SystemPrincipal
parameter_list|()
block|{ }
comment|//----------------------------------------------------------< Principal>---
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"system"
return|;
block|}
block|}
end_class

end_unit

