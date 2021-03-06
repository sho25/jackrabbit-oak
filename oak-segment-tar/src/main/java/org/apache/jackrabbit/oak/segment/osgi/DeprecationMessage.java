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
name|segment
operator|.
name|osgi
package|;
end_package

begin_class
specifier|final
class|class
name|DeprecationMessage
block|{
specifier|private
specifier|static
specifier|final
name|String
name|MOVED_PID_FORMAT
init|=
literal|"Deprecated configuration detected!\n\n"
operator|+
literal|"  A configuration for %s\n"
operator|+
literal|"  was detected. The oak-segment bundle used to contain this component,\n"
operator|+
literal|"  but the bundle is now deprecated and should not be included in your\n"
operator|+
literal|"  deployment. The oak-segment-tar bundle exposes an equivalent and improved\n"
operator|+
literal|"  functionality but you need to rename your configuration to target the\n"
operator|+
literal|"  new component using the PID %s.\n"
decl_stmt|;
specifier|private
name|DeprecationMessage
parameter_list|()
block|{}
specifier|static
name|String
name|movedPid
parameter_list|(
name|String
name|oldPid
parameter_list|,
name|String
name|newPid
parameter_list|)
block|{
return|return
name|String
operator|.
name|format
argument_list|(
name|MOVED_PID_FORMAT
argument_list|,
name|oldPid
argument_list|,
name|newPid
argument_list|)
return|;
block|}
block|}
end_class

end_unit

