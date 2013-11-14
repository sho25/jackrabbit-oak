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
name|commit
package|;
end_package

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
comment|/**  * MoveInfo... TODO  */
end_comment

begin_class
specifier|public
class|class
name|MoveInfo
block|{
comment|/**      * Create a new {@code MoveInfo}      */
specifier|public
name|MoveInfo
parameter_list|()
block|{     }
specifier|public
name|void
name|addMove
parameter_list|(
annotation|@
name|Nonnull
name|String
name|sourcePath
parameter_list|,
annotation|@
name|Nonnull
name|String
name|destPath
parameter_list|)
block|{
comment|// TODO
block|}
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
comment|// TODO
return|return
literal|true
return|;
block|}
specifier|public
name|boolean
name|isMoveDestination
parameter_list|(
name|String
name|path
parameter_list|)
block|{
comment|// TODO
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|isMoveSource
parameter_list|(
name|String
name|path
parameter_list|)
block|{
comment|// TODO
return|return
literal|false
return|;
block|}
specifier|public
name|void
name|clear
parameter_list|()
block|{
comment|// TODO
block|}
block|}
end_class

end_unit

