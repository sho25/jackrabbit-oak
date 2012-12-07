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
name|api
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Function
import|;
end_import

begin_comment
comment|/**  * A path resolver is responsible for resolving {@link TreeLocation}s for the elements  * in a path given its respective parent tree location.  *<p/>  * Each element in the iterator corresponds to an element of a path from the root  * to leaf. Each element is a {@link Function} mapping from the current tree location  * to the next one. The particulars of the mapping is determined on how implementations  * of this interface interpret the corresponding path element.  */
end_comment

begin_interface
specifier|public
interface|interface
name|PathResolver
extends|extends
name|Iterable
argument_list|<
name|Function
argument_list|<
name|TreeLocation
argument_list|,
name|TreeLocation
argument_list|>
argument_list|>
block|{ }
end_interface

end_unit

