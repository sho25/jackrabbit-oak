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
name|lifecycle
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
name|NodeStore
import|;
end_import

begin_comment
comment|/**  * {@code DefaultMicroKernelTracker} is a default implementation of all methods  * specified in {@link MicroKernelTracker}. The methods immediately return and do  * nothing.  *</p>  * This class can be used when a tracker only wants to get callbacks for some of  * the life cycle events and does not want to implement all methods specified  * in {@link MicroKernelTracker}. This also guarantees forward compatibility when  * new methods are introduced in later versions.  */
end_comment

begin_class
specifier|public
class|class
name|DefaultMicroKernelTracker
implements|implements
name|MicroKernelTracker
block|{
annotation|@
name|Override
specifier|public
name|void
name|available
parameter_list|(
name|NodeStore
name|store
parameter_list|)
block|{     }
block|}
end_class

end_unit

