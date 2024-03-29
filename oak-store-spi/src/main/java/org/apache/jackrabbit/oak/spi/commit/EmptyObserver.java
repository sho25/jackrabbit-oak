begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|state
operator|.
name|NodeState
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
name|NotNull
import|;
end_import

begin_comment
comment|/**  * Basic content change observer that doesn't do anything. Useful as a  * "null object" for cases where another observer has not been configured,  * thus avoiding an extra {@code null} check when invoking the observer.  */
end_comment

begin_class
specifier|public
class|class
name|EmptyObserver
implements|implements
name|Observer
block|{
comment|/**      * Static instance of this class, useful as a "null object".      */
specifier|public
specifier|static
specifier|final
name|EmptyObserver
name|INSTANCE
init|=
operator|new
name|EmptyObserver
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|contentChanged
parameter_list|(
annotation|@
name|NotNull
name|NodeState
name|root
parameter_list|,
annotation|@
name|NotNull
name|CommitInfo
name|info
parameter_list|)
block|{
comment|// do nothing
block|}
block|}
end_class

end_unit

