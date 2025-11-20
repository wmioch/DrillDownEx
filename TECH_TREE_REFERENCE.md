# Drill Down - Technology Research Tree Reference

## Overview
This document provides a complete reference for the technology research tree, showing prerequisites, research costs, and buildings unlocked by each research.

---

## Research Tree Structure

### **TIER 1: Starting Technologies** (Available from Start)

#### **Routers**
- **ID:** 1
- **Prerequisites:** Start
- **Research Time:** 45 seconds
- **Cost:** 50 Iron Ingot + 80 Stone Brick
- **Unlocks Buildings:**
  - Filter
  - Distributor

#### **OreProcessing**
- **ID:** 2
- **Prerequisites:** Start
- **Research Time:** 37 seconds
- **Cost:** 60 Iron Ore + 50 Copper Ore
- **Unlocks Buildings:**
  - RockCrusher
  - Mixer
  - Compactor
  - BallMill
  - Polarizer (requires Magnetism)

#### **Metalworking**
- **ID:** 3
- **Prerequisites:** Start
- **Research Time:** 55 seconds
- **Cost:** 75 Iron Ingot + 50 Copper Ingot
- **Unlocks Buildings:**
  - WireDrawer
  - RollingMachine
  - TubeBender

#### **SteelProduction**
- **ID:** 4
- **Prerequisites:** Start
- **Research Time:** 40 seconds
- **Cost:** 30 Iron Ingot
- **Unlocks Buildings:**
  - BlastFurnace
  - SawMill

#### **CharcoalProduction**
- **ID:** 8
- **Prerequisites:** Start
- **Research Time:** 35 seconds
- **Cost:** 75 Wood
- **Unlocks Buildings:**
  - CharcoalMound

---

### **TIER 2: Early Development**

#### **WaterUsage**
- **ID:** 6
- **Prerequisites:** Metalworking
- **Research Time:** 50 seconds
- **Cost:** 50 Steel Ingot + 30 Copper Tube
- **Unlocks Buildings:**
  - GroundwaterPump
  - Tank
  - CopperTube
  - Condenser
  - Boiler

#### **Magnetism**
- **ID:** 12
- **Prerequisites:** Metalworking
- **Research Time:** 25 seconds
- **Cost:** 150 Iron Ingot
- **Unlocks Buildings:** (none directly)

#### **BetterStorage**
- **ID:** 5
- **Prerequisites:** SteelProduction
- **Research Time:** 46 seconds
- **Cost:** 60 Scaffolding + 90 Stone Brick
- **Unlocks Buildings:**
  - Warehouse
  - Barrel

#### **MineExpansion**
- **ID:** 9
- **Prerequisites:** SteelProduction + WaterUsage
- **Research Time:** 50 seconds
- **Cost:** 150 Iron Ingot + 10 Steel Plate
- **Unlocks Buildings:**
  - ShaftDrill
  - AirPurifier

#### **ConsiderateConstruction**
- **ID:** 11
- **Prerequisites:** SteelProduction
- **Research Time:** 45 seconds
- **Cost:** 30 Scaffolding + 60 Brick + 95 Steel Ingot
- **Unlocks Buildings:** (none directly)

---

### **TIER 3: Industrial Power**

#### **Electricity**
- **ID:** 13
- **Prerequisites:** Magnetism
- **Research Time:** 55 seconds
- **Cost:** 100 Copper Wire + 10 Carbon Block + 8 Magnet + 5 Bronze Plate
- **Unlocks Buildings:**
  - Assembler
  - ArcWelder
  - CopperCable
  - ElectricConveyor
  - ElectricConveyorCore
  - Capacitor (requires BetterStorage)
  - VacuumPump (requires Routers)
  - Substation (requires Routers)

---

### **TIER 4: Advanced Processing**

#### **MineralExtraction**
- **ID:** 15
- **Prerequisites:** Electricity
- **Research Time:** 105 seconds
- **Cost:** 350 Stone Dust
- **Unlocks Buildings:**
  - Centrifuge

#### **HighTech**
- **ID:** 22
- **Prerequisites:** Electricity + WaterUsage
- **Research Time:** 50 seconds
- **Cost:** 40 Machine Frame + 60 Steel Wire
- **Unlocks Buildings:**
  - InductionFurnace
  - Excavator
  - DigitalStorage (requires BetterStorage)
  - Blueprints (reading tech tree)
  - HighPower technologies
  - AdvancedTransport technologies

---

### **TIER 5: Specialized Technologies**

#### **WaferGrowth**
- **ID:** 16
- **Prerequisites:** MineralExtraction
- **Research Time:** 65 seconds
- **Cost:** 90 Silicon Dust
- **Unlocks Buildings:**
  - Crucible
  - SolarPanel
  - SolarPanelOutlet

#### **Boosting**
- **ID:** 21
- **Prerequisites:** MineralExtraction
- **Research Time:** 50 seconds
- **Cost:** 240 Bronze Plate + 40 Battery
- **Unlocks Buildings:**
  - Booster

#### **OilProcessing**
- **ID:** 14
- **Prerequisites:** MineExpansion + HighTech
- **Research Time:** 70 seconds
- **Cost:** 70 Steel Tube + 4 Dynamo
- **Unlocks Buildings:**
  - OilWell
  - Polymerizer
  - Refinery
  - DistillationColumn
  - SteelTube
  - Valve (requires Routers)
- Assembler recipe: Advanced Servo (needs Blueprints + AdvancedTransport; consumes heavy Lubricant alongside catalysts for late-game turbines and precision frames)
- Dedicated Advanced Servo Assembler structure mirrors the servo recipe with its own build slot (OilProcessing tier)

#### **SolarPower**
- **ID:** 19
- **Prerequisites:** WaferGrowth
- **Research Time:** 25 seconds
- **Cost:** 2 Silicon Wafer + 90 Glass + 300 Copper Wire + 25 Battery
- **Unlocks Buildings:**
  - SolarPanel (already listed in WaferGrowth)
  - SolarPanelOutlet (already listed in WaferGrowth)

#### **HighPower**
- **ID:** 20
- **Prerequisites:** HighTech
- **Research Time:** 70 seconds
- **Cost:** 65 Steel Cable + 150 Tin Plate + 10 Battery
- **Unlocks Buildings:**
  - PowerPole
  - AnchorPortal
  - SuperCapacitor (requires BetterStorage)
  - GasTurbine (requires OilProcessing)

#### **Blueprints**
- **ID:** 23
- **Prerequisites:** HighTech
- **Research Time:** 40 seconds
- **Cost:** 250 Wood + 250 Iron Plate
- **Unlocks Buildings:** (structural/UI enhancement)

#### **AdvancedTransport**
- **ID:** 24
- **Prerequisites:** HighTech
- **Research Time:** 40 seconds
- **Cost:** 80 Tin Plate + 60 Steel Plate + 120 Wood Plank
- **Unlocks Buildings:**
  - FillingMachine
  - BarrelDrainer
  - Stacker

---

### **TIER 6: Advanced Manufacturing**

#### **PlasticMolding**
- **ID:** 17
- **Prerequisites:** OilProcessing
- **Research Time:** 78 seconds
- **Cost:** 200 Plastic Beads
- **Unlocks Buildings:**
  - InjectionMolder
  - ReliefValve

---

### **TIER 7: High-Tech Assembly**

#### **ComponentAssembly**
- **ID:** 18
- **Prerequisites:** PlasticMolding + WaferGrowth
- **Research Time:** 98 seconds
- **Cost:** 30 Silicon Wafer + 20 Plastic Casing + 15 Gold Dust + 250 Tin Wire
- **Unlocks Buildings:**
  - DeviceFabricator

---

## Buildings by Research Requirement

### Available from Start
- Mine
- Lumberjack
- Conveyor
- ConveyorBridge
- BrickChannel
- Storage
- Carpenter
- Furnace
- IngotMold
- Mason
- Kiln
- Hopper
- ScienceLab

### Routers
- Filter
- Distributor

### OreProcessing
- RockCrusher
- Mixer
- Compactor
- BallMill

### Metalworking
- WireDrawer
- RollingMachine
- TubeBender

### SteelProduction
- BlastFurnace
- SawMill

### CharcoalProduction
- CharcoalMound

### WaterUsage
- GroundwaterPump
- Tank
- CopperTube
- Condenser
- Boiler

### BetterStorage
- Warehouse
- Barrel

### MineExpansion
- ShaftDrill
- AirPurifier

### Electricity
- Assembler
- ArcWelder
- CopperCable
- ElectricConveyor
- ElectricConveyorCore
- VacuumPump (also needs Routers)
- Substation (also needs Routers)
- Capacitor (also needs BetterStorage)

### MineralExtraction
- Centrifuge

### WaferGrowth
- Crucible
- SolarPanel
- SolarPanelOutlet

### Boosting
- Booster

### OilProcessing
- OilWell
- Polymerizer
- Refinery
- DistillationColumn
- SteelTube
- Valve (also needs Routers)

### PlasticMolding
- InjectionMolder
- ReliefValve

### ComponentAssembly
- DeviceFabricator

### HighTech
- InductionFurnace
- Excavator
- DigitalStorage (also needs BetterStorage)

### HighPower
- PowerPole
- AnchorPortal
- SuperCapacitor (also needs BetterStorage)
- GasTurbine (also needs OilProcessing)

### AdvancedTransport
- FillingMachine
- BarrelDrainer
- Stacker

---

## Quick Reference: Research Chains

### **Mining & Extraction Path**
```
Start → SteelProduction → MineExpansion → OilProcessing → (various)
Start → OreProcessing → (processors)
```

### **Power Generation Path**
```
Start → Metalworking → Magnetism → Electricity → (power buildings)
Start → Metalworking → WaterUsage → (water-based power)
```

### **High-Tech Path**
```
Electricity → MineralExtraction → WaferGrowth → ComponentAssembly → DeviceFabricator
Electricity + WaterUsage → HighTech → (advanced buildings)
```

### **Storage & Logistics Path**
```
Start → SteelProduction → BetterStorage → (storage buildings)
Start → Routers → (distribution buildings)
```

---

## Research Costs Summary (Total Item Cost)

| Research | Cost (items) | Time (s) |
|----------|------------|----------|
| Routers | 130 | 45 |
| OreProcessing | 110 | 37 |
| Metalworking | 125 | 55 |
| SteelProduction | 30 | 40 |
| CharcoalProduction | 75 | 35 |
| WaterUsage | 80 | 50 |
| Magnetism | 150 | 25 |
| BetterStorage | 150 | 46 |
| MineExpansion | 160 | 50 |
| ConsiderateConstruction | 185 | 45 |
| Electricity | 123 | 55 |
| MineralExtraction | 350 | 105 |
| WaferGrowth | 90 | 65 |
| OilProcessing | 74 | 70 |
| PlasticMolding | 200 | 78 |
| ComponentAssembly | 315 | 98 |
| SolarPower | 417 | 25 |
| HighPower | 225 | 70 |
| HighTech | 100 | 50 |
| Boosting | 280 | 50 |
| Blueprints | 500 | 40 |
| AdvancedTransport | 260 | 40 |

---

Generated from codebase: `core/src/de/dakror/quarry/game/Science.java`
