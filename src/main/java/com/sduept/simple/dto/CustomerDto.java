package com.sduept.simple.dto;

import com.sduept.simple.entity.Customer;
import com.sduept.simple.entity.MachineCode;

import java.util.List;

public class CustomerDto extends Customer {

    private List<MachineCode> machineCodes;

    public List<MachineCode> getMachineCodes() {
        return machineCodes;
    }

    public void setMachineCodes(List<MachineCode> machineCodes) {
        this.machineCodes = machineCodes;
    }
}
