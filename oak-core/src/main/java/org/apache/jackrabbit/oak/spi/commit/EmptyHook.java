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
name|api
operator|.
name|CommitFailedException
import|;
end_import

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

begin_comment
comment|/**  * Basic commit hook implementation that by default doesn't do anything.  * This class has a dual purpose:  *<ol>  *<li>The static {@link #INSTANCE} instance can be used as a "null object"  * in cases where another commit hook has not been configured, thus avoiding  * the need for extra code for such cases.</li>  *<li>Other commit hook implementations can extend this class and gain  * improved forwards-compatibility to possible changes in the  * {@link CommitHook} interface. For example if it is later decided that  * new arguments are needed in the hook methods, this class is guaranteed  * to implement any new method signatures in a way that falls gracefully  * back to any earlier behavior.</li>  *</ol>  */
end_comment

begin_class
specifier|public
class|class
name|EmptyHook
implements|implements
name|CommitHook
block|{
comment|/**      * Static instance of this class, useful as a "null object".      */
specifier|public
specifier|static
specifier|final
name|CommitHook
name|INSTANCE
init|=
operator|new
name|EmptyHook
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|NodeState
name|processCommit
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|,
name|CommitInfo
name|info
parameter_list|)
throws|throws
name|CommitFailedException
block|{
return|return
name|after
return|;
block|}
block|}
end_class

end_unit

