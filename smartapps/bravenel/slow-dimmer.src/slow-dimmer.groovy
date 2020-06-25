/**
     *  Slow Dimmer
     *
     *  Copyright 2015 Bruce Ravenel
     *
     *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
     *  in compliance with the License. You may obtain a copy of the License at:
     *
     *      http://www.apache.org/licenses/LICENSE-2.0
     *
     *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
     *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
     *  for the specific language governing permissions and limitations under the License.
     *
     */
    definition(
        name: "Slow Dimmer",
        namespace: "bravenel",
        author: "Bruce Ravenel",
        description: "Slowly reduce dim level",
        category: "My Apps",
        iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
        iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
        iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")
    
    
    preferences {
    	section("Select dimmers to slowly dim...") {
    		input "dimmers", "capability.switchLevel", title: "Which?", required: true, multiple: true
    	}
        
        section("Over how many minutes to dim...") {
        	input "minutes", "number", title: "Minutes?", required: true, multiple: false
        }
        
        section("Select momentary button to launch...") {
        	input "trigger", "capability.momentary", title: "Which?", required: true
        }
    }
    
    def installed() {
    	initialize()
    }
    
    def updated() {
    	unsubscribe()
    	initialize()
    }
    
    def initialize() {
    	subscribe(trigger, "switch.on", triggerHandler)
    }
    
def triggerHandler(evt) {
    if(dimmers[0].currentSwitch == "off") state.currentLevel = 0
    else state.currentLevel = dimmers[0].currentLevel
    if(minutes == 0) return
    state.dimStep = state.currentLevel / minutes
    state.dimLevel = state.currentLevel
    dimStep()
}

def dimStep() {
    if(state.currentLevel > 0) {
        state.dimLevel = state.dimLevel - state.dimStep
        state.currentLevel = state.dimLevel.toInteger()
    	dimmers.setLevel(state.currentLevel)
        runIn(60,dimStep)
    } else dimmers.off()
}